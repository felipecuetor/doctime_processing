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

with open('../../../data/dblp_jour_revisit_output.csv', 'w') as file:
    file.write("title,authors,date,context,external_reference,url"+"\n")
driver = webdriver.Chrome()

all_urls = []
with open('../../../data/dblp_output_old.csv', 'r') as file:
    for line in file:
        line_split = line.split(",")
        url = return_string(line_split[5])
        if url.startswith("https://dblp.org/db/journals"):
            all_urls.append(return_string(url))

for href in all_urls[1:]:
    try:
        print href
        driver.get(href)
        time.sleep(1)
        document_list = driver.find_elements_by_class_name("entry")

        try:
            external_links = "isbn:"+clean_string(document_list[0].find_elements_by_css_selector("span[itemprop='isbn']")[0].text)
        except Exception as e:
            print e
            external_links = ""
        context = driver.find_element_by_id("breadcrumbs").find_elements_by_tag_name("span")[5].text
        for document_element in document_list:
            print document_element.text
            date = document_element.find_elements_by_css_selector("meta[itemprop='datePublished']")[0].get_attribute("content")+"/1/1"
            title = document_element.find_elements_by_class_name("title")[0].text
            authors_elements = document_element.find_elements_by_css_selector("span[itemtype='http://schema.org/Person']")
            authors = ""
            for author_element in authors_elements:
                authors = authors+clean_string(author_element.text)+";"
            authors = authors[:len(authors)-1]
            line = ""
            line=line+clean_string(title.encode('utf-8'))+","
            line=line+authors.encode('utf-8')+","
            line=line+clean_string(date.encode('utf-8'))+","
            line=line+clean_string(context.encode('utf-8'))+","
            line=line+external_links.encode('utf-8') +","
            line=line+clean_string(href.encode('utf-8'))+"\n"
            with open('../../../data/dblp_jour_revisit_output.csv', 'a') as file:
                file.write(line)
                file.close()
    except Exception as e:
        print "Error at "+href
        print e
        file.close()
