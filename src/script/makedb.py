import sys
import os
import sqlite3

i = 0

dbfile = sys.argv[1]

conn = sqlite3.connect(dbfile)
conn.execute("BEGIN TRANSACTION;");

rows = []

for line in sys.stdin:
	cols = line.strip().split('\t')
	
	name = cols[0].strip()
	sequence = cols[9].strip()
	
	tup = name, sequence
	rows.append(tup)
	
	i += 1
	if (i % 10000 == 0):
		conn.executemany("insert into bamfile values (?, ?)", rows)
		rows = []

conn.commit()
	
