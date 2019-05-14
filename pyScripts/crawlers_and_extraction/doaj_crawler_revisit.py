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

with open('../../../data/doaj_output.csv', 'w') as file:
    file.write("title,authors,date,context,external_reference,url"+"\n")
driver = webdriver.Chrome()

all_urls = []
with open('../../../data/doaj_output_old.csv', 'r') as file:
    for line in file:
        line_split = line.split(",")
        url = return_string(line_split[5])
        all_urls.append(url)
for href in all_urls[1:]:
    try:
        driver.get(href)
        time.sleep(1)
        total_meta_list = driver.find_elements_by_tag_name("meta")
        date = ""
        for meta in total_meta_list:
            meta_name = meta.get_attribute("name")
            if(str(meta_name).startswith("citation_publication_date")):
                date = meta.get_attribute("content")
        total_link_list = driver.find_elements_by_tag_name("a")
        doi_string = ""
        for link in total_link_list:
            link_href = link.get_attribute("href")
            if(str(link_href).startswith("https://doi.org")):
                doi_string = "doi:"+link.text+";"
        content = driver.find_elements_by_class_name("content")[0]
        title = clean_string(content.find_elements_by_tag_name("h1")[0].text)
        rows = content.find_elements_by_class_name("row-fluid")
        journal_and_publish = rows[2]
        authors_and_editorials = rows[3]
        context = clean_string(journal_and_publish.find_elements_by_class_name("with-borders")[1].find_elements_by_tag_name("p")[0].text)
        external_links = re.sub(r'\([^()]*\)', '',clean_string(journal_and_publish.find_elements_by_class_name("with-borders")[0].find_elements_by_tag_name("p")[2].text))
        external_links = external_links.replace("ISSN<#><#> ", "")
        try:
            external_links_split = external_links.split(" <$<$< ")
            external_links = "issn:"+external_links_split[0]+";issn:"+external_links_split[1]
        except:
            external_links = "issn:"+external_links
        external_links = external_links.replace(" ","")

        external_links = doi_string+external_links
        authors = re.sub(r'\([^()]*\)', '',clean_string(authors_and_editorials.find_elements_by_class_name("box")[0].find_elements_by_tag_name("p")[1].text.replace("\n",";")).replace("<$<$<",";"))
        line = ""
        line=line+title.encode('utf-8')+","
        line=line+ authors.encode('utf-8')+","
        line=line+ date.encode('utf-8')+","
        line=line+ context.encode('utf-8')+","
        line=line+ external_links.encode('utf-8') +","
        line=line+ clean_string(href.encode('utf-8'))+"\n"
        with open('../../../data/doaj_output.csv', 'a') as file:
            file.write(line)
            file.close()
    except Exception as e:
        print "Error at "+href
        print e
        file.close()
