## FETCH And Send Module

### Locally stored data is queried, fetched, and send containing source, destination, timestamp, and test results through a customized tool, automated to run with time interval through Cron [8] for centralized collection. First, raw time series data is extracted from measurements collected through tools such as BWCTL Data (TCP/UDP throughput)  and OWAMP (one-way delay) by using various APIs, and after intital stage parsing, transferred from SmartX Boxes to the centralized SmartX Visibility Center. Note that this function is run independently on each box.  
