def getDivisions(layers,divisions)
	answers = Array[]
	total = layers * (layers + 1) / 2
	partition = (total / divisions).floor	
	runningPartition = partition
	runningTotal = 0
	for i in (1..layers)
		runningTotal += i
		if runningTotal >= runningPartition
			runningPartition += partition
			answers += [i]
		end
	end
	return answers
end

puts getDivisions(20,5)