package cn.j1angvei.castk2.anno;

import cn.j1angvei.castk2.util.FileUtil;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * convert gtf annotation file into unified annotation file with 5 kinds, exon, intron, upstream, downstream, intergenic
 * Created by Wayne on 2/26 0026.
 */
public class GenomicElement {
    private List<Intergenic> mIntergenics;
    private List<Upstream> mUpstreams;
    private List<Exon> mExons;
    private List<Intron> mIntrons;
    private List<DownStream> mDownStreams;
    private List<Gene> mGenes;

    public GenomicElement(String annoFile) {
        System.out.println("Constructor Genomic Element " + annoFile);
        mGenes = new ArrayList<>();
        mExons = new ArrayList<>();
        mIntrons = new ArrayList<>();
        mUpstreams = new ArrayList<>();
        mDownStreams = new ArrayList<>();
        mIntergenics = new ArrayList<>();
        parseAnnotationFile(annoFile);
    }

    public List<Intergenic> getIntergenics() {
        return mIntergenics;
    }

    public List<Upstream> getUpstreams() {
        return mUpstreams;
    }

    public List<Exon> getExons() {
        return mExons;
    }

    public List<Intron> getIntrons() {
        return mIntrons;
    }

    public List<DownStream> getDownStreams() {
        return mDownStreams;
    }

    public List<Gene> getGenes() {
        return mGenes;
    }

    private void parseAnnotationFile(String annoFileName) {
        System.out.println("parse Annotation File");
        //parse gene and exon first, cause they are already in the annotation file
        parseGeneAndExon(annoFileName);
        //parse the remaining region, intron,upstream, downstream and intergenic
        parseRestRegion();
        //sort all regions
        sortAllSets();
    }

    private void parseGeneAndExon(String annoFileName) {
        System.out.println("Parse Gene and Exon");
        List<String> lines = FileUtil.readLineIntoList(annoFileName, true);
        for (String eachLine : lines) {
            String[] annoInfo = eachLine.split("\b+");
            if (annoInfo.length >= 4) {
                //gtf file
                String annoType = annoInfo[2].toLowerCase();
                switch (annoType) {
                    case "gene":
                        Gene gene = new Gene(annoInfo[0], annoInfo[3], annoInfo[4]);
                        mGenes.add(gene);
                        break;
                    case "exon":
                        Exon exon = new Exon(annoInfo[0], annoInfo[3], annoInfo[4]);
                        mExons.add(exon);
                        break;
                    default:
                        break;
                }
            }

        }
        //sort genes and exons
        Collections.sort(mGenes);
        Collections.sort(mExons);
        //add exon to its parent gene
        for (Exon exon : mExons) {
            for (Gene gene : mGenes) {
                if (Region.isOverlapped(exon, gene)) {
                    gene.addExon(exon);
                }
            }
        }
    }

    private void parseRestRegion() {
        System.out.println("Parse rest region");
        //parse intron
        for (Gene gene : mGenes) {
            Exon lastExon = null;
            for (Exon exon : gene.getExons()) {
                if (lastExon != null) {
                    Intron intron = new Intron(lastExon, exon);
                    gene.addIntron(intron);
                }
                lastExon = exon;
            }
        }
        //parse upstream and downstream
        for (Gene gene : mGenes) {
            Upstream upstream = new Upstream(gene);
            mUpstreams.add(upstream);
            DownStream downStream = new DownStream(gene);
            mDownStreams.add(downStream);
        }
        //parse intergenic
        Collections.sort(mUpstreams);
        Collections.sort(mDownStreams);
        for (int i = 1; i < mGenes.size(); i++) {
            Intergenic intergenic = new Intergenic(mDownStreams.get(i - 1), mUpstreams.get(i));
            mIntergenics.add(intergenic);
        }
    }

    private void sortAllSets() {
        //sort genes
        Collections.sort(mGenes);
        //sort exons in each gene
        for (Gene gene : mGenes) {
            Collections.sort(gene.getExons());
        }
        //sort intron in each gene
        for (Gene gene : mGenes) {
            Collections.sort(gene.getIntrons());
        }
        //sort up and down stream
        Collections.sort(mUpstreams);
        Collections.sort(mDownStreams);
    }

    public void storeToFile(String fileName) {
        File file = new File(fileName);
        try {
            FileUtils.writeLines(file, mGenes);
            FileUtils.writeLines(file, mExons);
            FileUtils.writeLines(file, mIntrons);
            FileUtils.writeLines(file, mUpstreams);
            FileUtils.writeLines(file, mDownStreams);
            FileUtils.writeLines(file, mIntergenics);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
