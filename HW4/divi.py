def getDivisions(layers,divisions):
	answers = []
	total = layers * (layers + 1) / 2
	partition = total // divisions
	runningPartition = partition
	runningTotal = 0
	for i in range(1,layers + 1):
		runningTotal += i
		if runningTotal >= runningPartition:
			runningPartition += partition
			answers.append(i)
	return answers

print(getDivisions(20,25)," are your divisions.")