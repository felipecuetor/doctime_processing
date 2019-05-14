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

def process_springer(url):
    print url
    driver.get(url)
    citation_list = driver.find_elements_by_class_name("CitationContent")
    for citation in citation_list:
        citation_text = citation.text
        if ":" in citation_text:
            split_colon = citation_text.split(":")
            authors = split_colon[0]
            split_period = split_colon[1].split(".")
            title = split_period[0]
            link_list = citation.find_elements_by_tag_name("a")
            doi_context = ""
            for link in link_list:
                if "doi.org" in link.get_attribute("href"):
                    doi_context = "doi:"+clean_string(link.get_attribute("href").encode('utf-8'))
            reference_string = ""
            reference_string = reference_string+clean_string(url.encode('utf-8'))+","
            reference_string = reference_string+clean_string(title.encode('utf-8'))+","
            reference_string = reference_string+clean_string(title.encode('utf-8'))+","
            reference_string = reference_string+clean_string(authors.encode('utf-8'))+","
            reference_string = reference_string+doi_context
            with open('../../../data_final/doi_reference_extraction.csv', 'a') as write_file:
                write_file.write(reference_string+"\n")
                write_file.close()


with open('../../../data_final/doi_reference_extraction.csv', 'w') as file:
    file.write("url,reference_title,reference_authors,reference_external_reference"+"\n")
    file.close()

with open('../../../data_final/doi_list.csv', 'r') as file:
    for line in file:
        if "link.springer.com" in line:
            process_springer(line)
