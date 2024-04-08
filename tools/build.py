#!/usr/bin/env python3
# Copyright 2024 tison <wander4096@gmail.com>
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.


import json
import shutil
import subprocess
from pathlib import Path

if __name__ == '__main__':
    basedir = Path(__file__).parent.parent

    cmd = ['cargo', 'build', '--release', '--color=always']
    print('$ ' + subprocess.list2cmdline(cmd))
    subprocess.run(cmd, cwd=(basedir / 'lib'), check=True)

    cmd += ['--message-format=json']
    print('$ ' + subprocess.list2cmdline(cmd))
    result = subprocess.run(cmd, cwd=(basedir / 'lib'), check=True, capture_output=True)

    output = result.stdout.decode('utf8')
    output = output.strip()
    messages = list(map(json.loads, output.split('\n')))
    artifacts = []
    for message in messages:
        if message['reason'] == 'compiler-artifact' and message['target']['name'] == 'topaz':
            artifacts.extend(message['filenames'])

    dst = basedir / 'target' / 'classes'
    dst.mkdir(exist_ok=True, parents=True)
    for artifact in artifacts:
        shutil.copy2(artifact, dst)
