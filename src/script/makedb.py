import sys
import os
import sqlite3

#####################################################################
# makedb.py
#
# Populate sqlite3 database with
# unmapped read data.
#
# Usage:
# samtools view unmapped.bam | python makedb.py unmapped.bam.db
#
# The SAM format should be piped into the standard input.
# The first argument is the name of an existing sqlite3 database
# file which contains the following table:
#
# CREATE TABLE bamfile (name varchar(50), sequence varchar(100) );
#
# Brown University
# David Storch (dstorch@cs.brown.edu)
# December 2011
#
#####################################################################

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
		print i
		conn.executemany("insert into bamfile values (?, ?)", rows)
		rows = []

conn.commit()
	
