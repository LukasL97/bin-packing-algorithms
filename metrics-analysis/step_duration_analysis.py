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
    run_step_names = {
        'greedy': 'greedy-run-step',
        'localsearch': 'local-search-run-step'
    }
    return run_step_names[algorithm]


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
    print('Step durations sum: %r seconds' % (sum([time for (step, time) in step_durations]) / 1000))


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--runId', type=str, required=True)
    parser.add_argument('--algorithm', type=str, default='localsearch', choices=['greedy', 'localsearch'])
    parser.add_argument('--endpoint', type=str, default='http://localhost:9000/metrics')
    args = parser.parse_args()

    analyze_step_durations(args.runId, args.algorithm, args.endpoint)
