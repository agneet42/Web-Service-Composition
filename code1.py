from pyeda.inter import *

def code():
	x,y = map(exprvar, "yx")
	exp1 = x|y
	exp2 = Not(x) | Not(y)
	exp3 = Not(exp2)
	exp4 = exp1 & exp3
	cnfexp = exp4.to_cnf()
	print(cnfexp.satisfy_one())


code()