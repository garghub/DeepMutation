package edu.wm.cs.mutation.tester;

import edu.wm.cs.mutation.abstractor.MethodAbstractor;
import edu.wm.cs.mutation.abstractor.MethodTranslator;
import edu.wm.cs.mutation.extractor.MethodExtractor;
import edu.wm.cs.mutation.io.IOHandler;
import edu.wm.cs.mutation.mutator.MethodMutator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MutantTesterTest {

    public static void main(String[] args) {
//        String dataPath = "data/";
//        String projPath = dataPath + "WebServer/";
//        String srcPath = projPath + "src/";
//        String outPath = dataPath + "out/WebServer/";
//        String libPath = null;
//        int complianceLvl = 4;
//        boolean compiled = false;
//
//        MutantTester.setCompileCmd(System.getProperty("user.home") + "/IdeaProjects/DeepMutation/data/WebServer/compile.sh");
//        MutantTester.setTestCmd(System.getProperty("user.home") + "/IdeaProjects/DeepMutation/data/WebServer/test.sh");

        String dataPath = "data/";
        String projPath = dataPath + "Chart/1/b/";
        String srcPath = projPath + "source/";
        String outPath = dataPath + "out/Chart/1/b/";
        String libPath = dataPath + "spoonModel/lib/Chart";
        int complianceLvl = 4;
        boolean compiled = true;

        String defects4j = System.getProperty("user.home") + "/defects4j/framework/bin/defects4j";
        MutantTester.setCompileCmd(defects4j, "compile");
        MutantTester.setTestCmd(defects4j, "test");
        MutantTester.setCompileFailString("FAIL");
        MutantTester.setTestFailString("Failing");
        MutantTester.useBaseline(false);

        String idiomPath = dataPath + "idioms.csv";
        List<String> modelPaths = new ArrayList<>();
        modelPaths.add(dataPath + "models/50len_ident_lit/");

        String inputMethodPath = dataPath + "methods.input";
        boolean specified = true;
        HashSet<String> inputMethods = null;
        if (specified) {
            inputMethods = IOHandler.readInputMethods(inputMethodPath);
        }

        MethodExtractor.extractMethods(projPath, srcPath, libPath, complianceLvl, compiled, inputMethods);
        IOHandler.writeMethods(outPath, MethodExtractor.getRawMethodsMap(), false);

        MethodAbstractor.abstractMethods(MethodExtractor.getRawMethodsMap(), idiomPath);
        IOHandler.writeMethods(outPath, MethodAbstractor.getAbstractedMethods(), true);
        IOHandler.writeMappings(outPath, MethodAbstractor.getMappings());

        MethodMutator.mutateMethods(outPath, MethodAbstractor.getAbstractedMethods(), modelPaths);
        IOHandler.writeMutants(outPath, MethodMutator.getMutantsMap(), modelPaths, true);

        MethodTranslator.translateMethods(MethodMutator.getMutantsMap(), MethodAbstractor.getMappings(), modelPaths);
        IOHandler.writeMutants(outPath, MethodTranslator.getTranslatedMutantsMap(), modelPaths, false);

        IOHandler.createMutantFiles(outPath, MethodTranslator.getTranslatedMutantsMap(),
                MethodExtractor.getMethods(), modelPaths);

        MutantTester.testMutants(projPath, MethodTranslator.getTranslatedMutantsMap(),
                MethodExtractor.getMethods(), modelPaths);

        if (MutantTester.usingBaseline()) {
            IOHandler.writeBaseline(outPath, MutantTester.getCompileBaseline(), "compile");
            IOHandler.writeBaseline(outPath, MutantTester.getTestBaseline(), "test");
        }

        IOHandler.writeLogs(outPath, MutantTester.getCompileLogs(), modelPaths, "compile");
        IOHandler.writeLogs(outPath, MutantTester.getTestLogs(), modelPaths, "test");
        IOHandler.writeResults(outPath, MutantTester.getCompilable(), modelPaths, "compile");
        IOHandler.writeResults(outPath, MutantTester.getSuccessful(), modelPaths, "test");
    }

}
