#!/usr/local/bin/bash

awk -v n=$2 '{ print $n }' $1

