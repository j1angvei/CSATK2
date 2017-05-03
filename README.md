# CSATK (ChIP-Seq Analysis Toolkit)
## Introduction
## Usage
## Tutorial
### How to download and use CSATK
1. Download CSATK compressed file, [CSATK2-170503 release page](https://github.com/j1angvei/CSATK2/releases/tag/v2.0.170503);
2. Unzip the .tgz file, run `java -jar CSATK.jar -r` to restore CSATK structure;
3. Run `java -jar CSATK.jar -i` to install all relevant software(FastQC, BWA, SAMTools, etc.);
4. Place your raw data under **input** folder, genome reference file and annotation under **genome** folder;
5. Create your **input.json** file and put it under **config** folder (there is a template of input.json under the same folder);
6. Start the ChIP-Seq pipeline by running `java -jar CSATK.jar -p`.
### How to create your own **input.json** file

If you are familiar with [JSON](https://en.wikipedia.org/wiki/JSON) format, you can create it very easily using VIM (Linux) or Notepad (Windows). And all you need to is modify the input.json template in **config** folder.    

In case you have no idea what JSON format is or you just don't want to write the boilerplate, CSATK can help you create the input.json file, just double click the JAR file or run `java -jar CSATK.jar`.    

Under any environment with X Display support(Like Windows or Ubuntu Desktop) and [JRE](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html),you can start CSATK GUI to help you create input.json.  

Here is a simple illustration of using CSATK GUI to create input.json:
<img src='./raw/all_genomes_text.png' width=600/>
<img src='./raw/edit_genome_text.png' width=330/>
<img src='./raw/all_experiments_text.png' width=600/>
<img src='./raw/edit_experiment_text.png' width=330/>
<img src='./raw/generate_success_text.png' width=225/>
## About
## License