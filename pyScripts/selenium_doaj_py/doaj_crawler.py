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


with open('../../../data/doaj_output.csv', 'w') as file:
    file.write("title,authors,date,context,external_reference,url"+"\n")
driver = webdriver.Chrome()

all_urls = []
cont = 0
while(cont<4300):
    all_urls.append("https://doaj.org/search?source=%7B%22query%22%3A%7B%22filtered%22%3A%7B%22filter%22%3A%7B%22bool%22%3A%7B%22must%22%3A%5B%7B%22term%22%3A%7B%22index.classification.exact%22%3A%22Technology%22%7D%7D%5D%7D%7D%2C%22query%22%3A%7B%22query_string%22%3A%7B%22query%22%3A%22Colombia%22%2C%22default_field%22%3A%22index.country%22%2C%22default_operator%22%3A%22AND%22%7D%7D%7D%7D%2C%22from%22%3A"+str(cont)+"%2C%22size%22%3A200%7D#.XGhO0KC225v")
    cont+=200
cont = 0
while(cont<4000):
    all_urls.append("https://doaj.org/search?source=%7B%22query%22%3A%7B%22filtered%22%3A%7B%22filter%22%3A%7B%22bool%22%3A%7B%22must%22%3A%5B%7B%22term%22%3A%7B%22index.classification.exact%22%3A%22Science%22%7D%7D%5D%7D%7D%2C%22query%22%3A%7B%22query_string%22%3A%7B%22query%22%3A%22Colombia%22%2C%22default_field%22%3A%22index.country%22%2C%22default_operator%22%3A%22AND%22%7D%7D%7D%7D%2C%22from%22%3A"+str(cont)+"%2C%22size%22%3A200%7D#.XGhO0KC225v")
    cont+=200
for url in all_urls:
    driver.get(url)
    time.sleep(6)
    results_table = driver.find_element_by_id("facetview_results");
    results = results_table.find_elements_by_class_name("span12")
    results_str = []
    print "Initiating analisis of "+url
    for result in results:
        span = result.find_elements_by_class_name("title")[0]
        link = span.find_elements_by_tag_name("a")[0]
        href = link.get_attribute("href")
        results_str.append(href)
    for href in results_str:
        driver.get(href)
        time.sleep(3)
        content = driver.find_elements_by_class_name("content")[0]
        title = clean_string(content.find_elements_by_tag_name("h1")[0].text)
        rows = content.find_elements_by_class_name("row-fluid")
        journal_and_publish = rows[2]
        authors_and_editorials = rows[3]
        context = clean_string(journal_and_publish.find_elements_by_class_name("with-borders")[1].find_elements_by_tag_name("p")[0].text)
        external_links = clean_string(journal_and_publish.find_elements_by_class_name("with-borders")[0].find_elements_by_tag_name("p")[1].text)
        authors = re.sub(r'\([^()]*\)', '',clean_string(authors_and_editorials.find_elements_by_class_name("box")[0].find_elements_by_tag_name("p")[1].text).encode('utf-8'))
        date = ""
        line = title.encode('utf-8')+","+ authors+","+ date+","+ context.encode('utf-8')+","+ external_links.encode('utf-8')+","+ clean_string(href.encode('utf-8'))+"\n"
        with open('../../../data/doaj_output.csv', 'a') as file:
            file.write(line)
            file.close()
        print line
