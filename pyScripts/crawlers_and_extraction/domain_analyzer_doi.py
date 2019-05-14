domain_count = {}

with open('../../../data_final/doi_list.csv', 'r') as file:
    for line in file:
        directory_split = line.split(" ")[1].split("/")
        if directory_split[2] in domain_count:
            domain_count[directory_split[2]]=domain_count[directory_split[2]]+1
        else:
            domain_count[directory_split[2]] = 1
print domain_count

with open('../../../data_final/doi_count.csv', 'w') as file:
    file.close()

with open('../../../data_final/doi_count.csv', 'a') as file:
    for key in domain_count.keys():
        file.write(key+";"+str(domain_count[key])+"\n")
    file.close()
