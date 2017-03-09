package cn.j1angvei.castk2.anno;

import cn.j1angvei.castk2.util.FileUtil;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * convert gtf annotation file into unified annotation file with 5 kinds, exon, intron, upstream, downstream, intergenic
 * Created by Wayne on 2/26 0026.
 */
public class GenomicElement {
    private Set<Intergenic> mIntergenics;
    private Set<Upstream> mUpstreams;
    private Set<Exon> mExons;
    private Set<Intron> mIntrons;
    private Set<DownStream> mDownStreams;
    private Set<Gene> mGenes;

    public GenomicElement(String annoFile) {
        System.out.println("Constructor Genomic Element " + annoFile);
        mGenes = new TreeSet<>();
        mExons = new TreeSet<>();
        mIntrons = new TreeSet<>();
        mUpstreams = new TreeSet<>();
        mDownStreams = new TreeSet<>();
        mIntergenics = new TreeSet<>();
        //skip this function, cause it is not ready
//        parseAnnotationFile(annoFile);
    }

    private void parseAnnotationFile(String annoFileName) {
        //parse gene and exon first, cause they are already in the annotation file
        parseGeneAndExon(annoFileName);
        //parse the remaining region, intron,upstream, downstream and intergenic
        parseRestRegion();
    }

    private void updateExons(Exon exon) {
        Iterator<Exon> iterator = mExons.iterator();
        boolean shouldInsert = true;
        while (iterator.hasNext()) {
            Exon e = iterator.next();
            //not overlapped, check next existing exon
            if (!Region.isOverlapped(e, exon))
                continue;
            //exon overlapped, 2 situation:
            //1.do nothing, ignore this exon
            if (Region.isAWrapB(e, exon)) {
                shouldInsert = false;
                break;
                //2.since two exons overlapped, keep the larger one
            } else {
                shouldInsert = (exon.getEnd() - exon.getStart()) > (e.getEnd() - e.getStart());
                if (shouldInsert) iterator.remove();
                break;
            }
        }
        if (shouldInsert) mExons.add(exon);

    }

    private void parseGeneAndExon(String annoFileName) {
        List<String> lines = FileUtil.readLineIntoList(annoFileName);
        for (String eachLine : lines) {
            String[] annoInfo = eachLine.split("\t");
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
//                        mExons.add(exon);
                        updateExons(exon);
                        break;
                    default:
                        break;
                }
            }
        }
        //add exon to its parent gene
        for (Exon exon : mExons) {
            for (Gene gene : mGenes) {
                if (Region.isAInB(exon, gene)) {
                    gene.addExon(exon);
                }
            }
        }
    }

    private void parseRestRegion() {
        System.out.println("Parse rest region");
        //parse intron
        for (Gene gene : mGenes) {
            Set<Exon> allExons = gene.getExons();
            Iterator<Exon> iterator = allExons.iterator();
            Exon lastExon = null, curExon;
            while (iterator.hasNext()) {
                curExon = iterator.next();
                if (lastExon != null) {
                    Intron intron = new Intron(lastExon, curExon);
                    gene.getIntrons().add(intron);
                    mIntrons.add(intron);
                }
                lastExon = curExon;
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
        Iterator<Gene> iterator = mGenes.iterator();
        Gene lastGene = null, curGene;
        while (iterator.hasNext()) {
            curGene = iterator.next();
            //first gene in list or two genes not in same chromosome
            if (lastGene != null && lastGene.getChromosome().equals(curGene.getChromosome())) {
                long start = lastGene.getEnd() + 2000;
                long end = curGene.getStart() - 2000;
                if (start < end) {
                    Intergenic intergenic = new Intergenic(lastGene.getChromosome(), start, end);
                    mIntergenics.add(intergenic);
                }
            }
            lastGene = curGene;
        }
    }

    public void storeToFile(String fileName) {
        File file = new File(fileName);
        List<Region> all = new ArrayList<>();
        all.addAll(mExons);
        all.addAll(mIntrons);
        all.addAll(mUpstreams);
        all.addAll(mDownStreams);
        all.addAll(mIntergenics);
        Collections.sort(all);
        try {
            FileUtils.writeLines(file, all);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
