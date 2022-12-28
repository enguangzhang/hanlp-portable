/*
 * <author>Han He</author>
 * <email>me@hankcs.com</email>
 * <create-date>2018-07-29 8:49 PM</create-date>
 *
 * <copyright file="DemoPlane.java">
 * Copyright (c) 2018, Han He. All Rights Reserved, http://www.hankcs.com/
 * This source is subject to Han He. Please contact Han He for more information.
 * </copyright>
 */
package com.hankcs.zeg;

import java.io.IOException;
import java.util.List;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.mining.phrase.IPhraseExtractor;
import com.hankcs.hanlp.model.perceptron.CWSTrainer;
import com.hankcs.hanlp.model.perceptron.NERTrainer;
import com.hankcs.hanlp.model.perceptron.POSTrainer;
import com.hankcs.hanlp.model.perceptron.PerceptronLexicalAnalyzer;
import com.hankcs.hanlp.model.perceptron.PerceptronNERecognizer;
import com.hankcs.hanlp.model.perceptron.PerceptronPOSTagger;
import com.hankcs.hanlp.model.perceptron.PerceptronSegmenter;
import com.hankcs.hanlp.model.perceptron.model.LinearModel;

/**
 * 《自然语言处理入门》8.6.2 训练领域模型
 * 配套书籍：http://nlp.hankcs.com/book.php
 * 讨论答疑：https://bbs.hankcs.com/
 *
 * @author hankcs
 * @see <a href="http://nlp.hankcs.com/book.php">《自然语言处理入门》</a>
 * @see <a href="https://bbs.hankcs.com/">讨论答疑</a>
 */
public class CustomNerTrain
{
    static String TRAIN_CORPUS = "data/test/zeg/train.txt";
    static String NER_MODEL = TRAIN_CORPUS.replace("train.txt", "ner.bin");
    static String CWS_MODEL = NER_MODEL.replace("ner.bin", "cws.bin");
    static String POS_MODEL = NER_MODEL.replace("ner.bin", "pos.bin");

    public static void main(String[] args) throws IOException
    {
        NERTrainer trainer = new NERTrainer();
        //trainer.tagSet.nerLabels.clear(); // 不识别nr、ns、nt
        trainer.tagSet.nerLabels.add("nle"); // 目标是识别nle
        trainer.tagSet.nerLabels.add("npd"); // 目标是识别npd
        trainer.tagSet.nerLabels.add("nse"); // 目标是识别nse
        trainer.tagSet.nerLabels.add("nz"); // 目标是识别nse
        trainer.tagSet.nerLabels.add("ntc"); // 目标是识别nse
        trainer.tagSet.nerLabels.add("n"); // 目标是识别nse
        PerceptronNERecognizer recognizer = new PerceptronNERecognizer(trainer.train(TRAIN_CORPUS, NER_MODEL).getModel());

        // 在NER预测前，需要一个分词器，最好训练自同源语料库
        LinearModel cwsModel = new CWSTrainer().train(TRAIN_CORPUS, CWS_MODEL).getModel();
        PerceptronSegmenter segmenter = new PerceptronSegmenter(cwsModel);
        LinearModel posModel = new POSTrainer().train(TRAIN_CORPUS, POS_MODEL).getModel();
        PerceptronPOSTagger posTagger = new PerceptronPOSTagger(posModel);
        PerceptronLexicalAnalyzer analyzer = new PerceptronLexicalAnalyzer(segmenter, posTagger, recognizer);
        analyzer.enableTranslatedNameRecognize(false).enableCustomDictionary(false);
        /*System.out.println(analyzer.analyze("客户获取电话拨打提示没有权限"));
        System.out.println(analyzer.analyze("客户账号弹出账号行为异常"));
        System.out.println(analyzer.analyze("H端产品问题-面试快-重发确认到场短信-未收到短信"));
        System.out.println(analyzer.seg("H端产品问题-面试快-重发确认到场短信-未收到短信"));
        System.out.println(analyzer.segment("H端产品问题-面试快-重发确认到场短信-未收到短信"));
        System.out.println(analyzer.seg2sentence("H端产品问题-面试快-重发确认到场短信-未收到短信"));

        System.out.println(analyzer.analyze("C端产品问题-咨询修改简历-修改头像"));
        System.out.println(analyzer.segment("C端产品问题-咨询修改简历-修改头像"));
        System.out.println(analyzer.analyze("猎头合同审核： 上海品吉伟仕商务服务有限公司"));
        System.out.println(analyzer.analyze("猎聘自己猎头打电话说有企业看到求职者简历，不但不专业，还态度...。"));
        System.out.println(analyzer.segment("猎聘自己猎头打电话说有企业看到求职者简历，不但不专业，还态度...。"));
*/
        IPhraseExtractor extractor = new CustomizeMutualInformationEntropyPhraseExtractor(analyzer);
        List<String> phraseList = extractor.extractPhrase("H端产品问题-面试快-重发确认到场短信-未收到短信", 100);
        //List<String> phraseList = HanLP.extractPhrase("H端产品问题-面试快-重发确认到场短信-未收到短信", 100);
        System.out.println(phraseList);

        PerceptronPOSTagger perceptronPOSTagger = new PerceptronPOSTagger();

    }

    public static void main1(String[] args) throws IOException
    {
        NERTrainer trainer = new NERTrainer();
        //trainer.tagSet.nerLabels.clear(); // 不识别nr、ns、nt
        PerceptronNERecognizer recognizer = new PerceptronNERecognizer(
                trainer.train("data/test/pku98/199801.txt", "data/test/pku98/ner-tmp.bin").getModel());

        // 在NER预测前，需要一个分词器，最好训练自同源语料库
        LinearModel cwsModel =
                new CWSTrainer().train("data/test/pku98/199801.txt", "data/test/pku98/cws-tmp.bin").getModel();
        PerceptronSegmenter segmenter = new PerceptronSegmenter(cwsModel);
        LinearModel posModel =
                new POSTrainer().train("data/test/pku98/199801.txt", "data/test/pku98/pos-tmp.bin").getModel();
        PerceptronPOSTagger posTagger = new PerceptronPOSTagger(posModel);
        PerceptronLexicalAnalyzer analyzer = new PerceptronLexicalAnalyzer(segmenter, posTagger, recognizer);
        analyzer.enableTranslatedNameRecognize(false).enableCustomDictionary(false);
        System.out.println(analyzer.analyze("客户获取电话拨打提示没有权限"));
        System.out.println(analyzer.analyze("客户账号弹出账号行为异常"));
        System.out.println(analyzer.analyze("H端产品问题-面试快-重发确认到场短信-未收到短信"));
        System.out.println(analyzer.segment("H端产品问题-面试快-重发确认到场短信-未收到短信"));
        System.out.println(analyzer.analyze("C端产品问题-咨询修改简历-修改头像"));
        System.out.println(analyzer.segment("C端产品问题-咨询修改简历-修改头像"));
        System.out.println(analyzer.analyze("猎头合同审核： 上海品吉伟仕商务服务有限公司"));
        System.out.println(analyzer.analyze("猎聘自己猎头打电话说有企业看到求职者简历，不但不专业，还态度...。"));
        System.out.println(analyzer.segment("猎聘自己猎头打电话说有企业看到求职者简历，不但不专业，还态度...。"));

        List<String> phraseList = HanLP.extractPhrase("H端产品问题-面试快-重发确认到场短信-未收到短信", 100);
        System.out.println(phraseList);

    }
}
