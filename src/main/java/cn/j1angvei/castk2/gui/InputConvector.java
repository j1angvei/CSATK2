package cn.j1angvei.castk2.gui;

import cn.j1angvei.castk2.conf.Experiment;
import cn.j1angvei.castk2.conf.Genome;
import cn.j1angvei.castk2.conf.Input;
import cn.j1angvei.castk2.util.StrUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wayne on 5/3 0003.
 */
public class InputConvector {
    private static InputConvector INSTANCE;

    public static InputConvector getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new InputConvector();
        }
        return INSTANCE;
    }

    private InputConvector() {
    }

    public Input convert(List<GenomeModel> genomeModels, List<ExperimentModel> experimentModels) {
        Input input = new Input();
        input.setGenomes(convertGenomes(genomeModels));
        input.setExperiments(convertExperiments(experimentModels));
        return input;
    }


    private Genome convert(GenomeModel model) {
        Genome genome = new Genome();
        genome.setCode(model.getCode());
        genome.setName(model.getName());
        genome.setSize(model.getSize());
        genome.setFasta(model.getFasta());
        genome.setAnnotation(model.getGtf());
        return genome;
    }

    private List<Genome> convertGenomes(List<GenomeModel> models) {
        List<Genome> genomes = new ArrayList<>();
        for (GenomeModel model : models) {
            genomes.add(convert(model));
        }
        return genomes;
    }

    private Experiment convert(ExperimentModel model) {
        Experiment experiment = new Experiment();
        experiment.setCode(model.getCode());
        experiment.setGenomeCode(model.getGenomeCode());
        experiment.setFastq1(model.getFastq1());
        if (StrUtil.isInvalid(model.getFastq2())) {
            experiment.setFastq2("");
        } else {
            experiment.setFastq2(model.getFastq2());
        }
        if (StrUtil.isInvalid(model.getControl())){
            experiment.setControl("");
        }else {
        experiment.setControl(model.getControl());
        }
        experiment.setBroadPeak(model.isBroadPeak());
        return experiment;
    }

    private List<Experiment> convertExperiments(List<ExperimentModel> models) {
        List<Experiment> experiments = new ArrayList<>();
        for (ExperimentModel model : models) {
            experiments.add(convert(model));
        }

        return experiments;
    }
}
