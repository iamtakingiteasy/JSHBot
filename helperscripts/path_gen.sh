#!/bin/sh

dir="$(pwd)"

cd "$(dirname "$0")"
if echo "$PATH" | grep -q "$(pwd)/bin"; then
	echo export PATH=$PATH
else
	echo export PATH="$PATH:$(pwd)/bin"
fi

cd ..
echo export JSHBOT_ROOT="$(pwd)"

cd "$dir"
