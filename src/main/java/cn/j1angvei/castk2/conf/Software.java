package cn.j1angvei.castk2.conf;

import java.io.File;

/**
 * Created by Wayne on 2016/11/11.
 * represent all software used in ChIP_Seq analysis
 */
public enum Software {
    /**
     * as software swName,
     * archive compressed archive file swName,
     * software archive destination folder after uncompress, aka software's subDir
     * software executable
     */
    FASTQC(
            "fastqc",
            "FastQC_v0.11.5.zip",
            "FastQC",
            "fastqc"),
    TRIMMOMATIC(
            "trimmomatic",
            "Trimmomatic-0.36.zip",
            "Trimmomatic-0.36",
            "trimmomatic-0.36.jar"),
    BWA(
            "bwa",
            "bwa-0.7.13.tar.bz2",
            "bwa-0.7.13",
            "bwa"),
    SAMTOOLS(
            "samtools",
            "samtools-1.3.1.tar.bz2",
            "samtools-1.3.1",
            "samtools"),
    HOMER(
            "homer",
            "homer.v4.8.3.zip",
            "homer.v4.8.3",
            "bin" + File.separator),
    QUALIMAP(
            "qualimap",
            "qualimap_v2.2.zip",
            "qualimap_v2.2",
            "qualimap"),
    MACS2(
            "macs2",
            "MACS2-2.1.1.20160309.tar.gz",
            "MACS2-2.1.1.20160309",
            "bin" + File.separator + "macs2"),
    WEBLOGO(
            "weblogo",
            "weblogo.2.8.2.tar.gz",
            "weblogo",
            "seqlogo"),
    UCSC("ucsc",
            "ucsc-1.0.tar.gz",
            "ucsc-1.0",
            ""
    );

    private String swName;
    private String archive;
    private String destFolder;
    private String executable;

    Software(String swName, String archive, String destFolder, String executable) {
        this.swName = swName;
        this.archive = archive;
        this.destFolder = destFolder;
        this.executable = executable;
    }

    public String getSwName() {
        return swName;
    }

    public String getArchive() {
        return archive;
    }

    public String getDestFolder() {
        return destFolder;
    }

    public String getExecutable() {
        return executable;
    }
}
