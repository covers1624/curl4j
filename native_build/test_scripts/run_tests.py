#!/usr/bin/env python3
import platform
import subprocess
import os

if platform.system() == 'Darwin':
    to_build = [
        'build_macos_arm64',
        'build_macos_x64'
    ]
else:
    to_build = [
        'build_linux_x64-gnu',
        'build_linux_x64-musl',
        'build_linux_arm64-gnu',
        'build_linux_arm64-musl',
        'build_windows_x64'
    ]

states = {}

os.makedirs("./logs", exist_ok=True)

for script in to_build:
    process = subprocess.Popen(
        ['/bin/sh', f'./{script}.sh'],
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        stdin=subprocess.DEVNULL,
        text=True
    )
    with open(f'./logs/{script}.log', 'w') as log:
        for line in process.stdout:
            print(line, end='')
            log.write(line)
    process.wait()
    states[script] = (process.returncode == 0)

print()
print('Results:')
for script, code in states.items():
    print(f"{script} {code}")
