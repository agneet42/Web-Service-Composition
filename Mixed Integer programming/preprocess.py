import csv
import pandas as pd 

obj = csv.reader(open("working.csv","r"))
obj1 = csv.writer(open("preprocess.csv","w"))

for row in obj:
	serv_name = row[9]
	to_write = [serv_name] + row[0:8]
	obj1.writerow(to_write)

df = pd.read_csv('preprocess.csv',header=None)
ds = df.sample(frac=1)						# shuffling the dataset
ds.to_csv('preprocess1.csv',index=False)

