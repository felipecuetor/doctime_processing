from selenium import webdriver
import time
import re
#from selenium.webdriver.common.keys import keys

def clean_string(str_to_clean):
    clean_str = str_to_clean
    clean_str = clean_str.replace(":", "<#><#>")
    clean_str = clean_str.replace(";", "<$<$<")
    clean_str = clean_str.replace(",", ">*>*>")
    clean_str = clean_str.replace("\n", "")
    return clean_str

def return_string(str_to_clean):
    clean_str = str_to_clean
    clean_str = clean_str.replace( "<#><#>",":")
    clean_str = clean_str.replace("<$<$<",";")
    clean_str = clean_str.replace(">*>*>",",")
    return clean_str

driver = webdriver.Chrome()

with open('../../../data_final/doi_list.csv', 'w') as file:
    file.close()

doi_iter_list = []
with open('../../../data_final/unified_doaj_arxiv_ceur_dblp.csv', 'r') as file:
    for line in file:
        split_doc = line.split(",")
        external_link_split = split_doc[4].split(";")
        for link in external_link_split:
            if link.startswith("doi:"):
                try:
                    doi_value = link.split(":")[1]
                    driver.get("https://doi.org/"+doi_value)
                    time.sleep(1)
                    #doi_iter_list.append(driver.current_url)
                    with open('../../../data_final/doi_list.csv', 'a') as file:
                        file.write(doi_value+" "+driver.current_url+"\n")
                        file.close()
                except Exception as e:
                    print e
                    file.close()
