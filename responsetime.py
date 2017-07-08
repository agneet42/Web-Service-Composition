import csv
import statistics

obj = csv.reader(open("preprocess1.csv","r"))

div = 2506/7
count = 0
arr = []
sum_med = 0
for row in obj:
	arr.append(float(row[1]))
	count = count + 1
	if(count == div):
		sum_med = sum_med + statistics.median(arr)
		arr = []
		count = 0

