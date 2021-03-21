import argparse
import requests
import matplotlib.pyplot as plt
import numpy as np


def query_metrics(endpoint, body):
    response = requests.request(method='get', url=endpoint, json=body)
    if response.status_code == 200:
        return response.json()
    else:
        raise RuntimeError('Got response with status %d: %s' % (response.status_code, response.content))


def get_run_step_name(algorithm):
    if algorithm.startswith('greedy'):
        return 'greedy-run-step'
    elif algorithm.startswith('ls'):
        return 'local-search-run-step'
    else:
        raise RuntimeError('Runstep name unknown for algorithm %s' % algorithm)


def build_query_body(name, run_id):
    return {
        'name': name,
        'filters': [
            {
                'key': 'runId',
                'value': run_id
            }
        ]
    }


def get_step_from_metric(metric):
    tags = metric['tags']
    return [int(tag['value']) for tag in tags if tag['key'] == 'step'][0]


def get_time_in_ms_from_metric(metric):
    return metric['result']['mean'] / 1000000


def get_percentile(step_durations, q):
    sorted_step_durations = sorted(step_durations, key=lambda x: x[1])
    index = int(len(sorted_step_durations) * q)
    return sorted_step_durations[index][1]


def plot(step_durations):
    plt.plot(
        np.array([step for (step, time) in step_durations]),
        np.array([time for (step, time) in step_durations])
    )
    plt.xlabel('step')
    plt.ylabel('time in ms')
    plt.ylim(0, get_percentile(step_durations, 0.99) * 1.5)
    plt.show()


def analyze_step_durations(run_id, algorithm, endpoint):
    run_step_name = get_run_step_name(algorithm)
    query = build_query_body(run_step_name, run_id)
    metrics = query_metrics(endpoint, query)
    step_durations = sorted(
        [(get_step_from_metric(m), get_time_in_ms_from_metric(m)) for m in metrics],
        key=lambda x: x[0]
    )
    plot(step_durations)
    print('Step durations sum: %r ms' % (sum([time for (step, time) in step_durations])))


def analyze_timer(name, runId, endpoint):
    query = build_query_body(name, runId)
    metric = query_metrics(endpoint, query)[0]
    return metric['result']['count'], metric['result']['mean'] / 1000000


def analyze_local_search_geometry_based(run_id, endpoint):
    timers = [
        'ls-geometry-evaluate',
        'box-merge-neighborhood',
        'box-pull-up-neighborhood',
        'coarse-multiple-box-pull-up-neighborhood',
        'entire-box-maximally-shifted-neighborhood'
    ]
    for timer in timers:
        count, time = analyze_timer(timer, run_id, endpoint)
        print('%s: count: %d, mean: %r ms, sum: %r ms' % (timer, count, time, count * time))


if __name__ == '__main__':
    algorithms = [
        'greedy_random',
        'greedy_size',
        'ls_geometry',
        'ls_overlap'
    ]
    parser = argparse.ArgumentParser()
    parser.add_argument('--runId', type=str, required=True)
    parser.add_argument('--algorithm', type=str, default='ls_geometry', choices=algorithms)
    parser.add_argument('--endpoint', type=str, default='http://localhost:9000/metrics')
    args = parser.parse_args()

    analyze_step_durations(args.runId, args.algorithm, args.endpoint)

    if (args.algorithm == 'ls_geometry'):
        analyze_local_search_geometry_based(args.runId, args.endpoint)
