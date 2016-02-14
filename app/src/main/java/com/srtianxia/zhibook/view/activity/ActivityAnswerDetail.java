package com.srtianxia.zhibook.view.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.srtianxia.zhibook.R;
import com.srtianxia.zhibook.app.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by srtianxia on 2016/2/15.
 */
public class ActivityAnswerDetail extends BaseActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.answer_question_title)
    TextView answerQuestionTitle;
    @Bind(R.id.card_answer_question_title)
    CardView cardAnswerQuestionTitle;
    @Bind(R.id.answer_detail_head)
    SimpleDraweeView answerDetailHead;
    @Bind(R.id.answer_detail_author)
    TextView answerDetailAuthor;
    @Bind(R.id.answer_detail_author_sign)
    TextView answerDetailAuthorSign;
    @Bind(R.id.answer_detail_praise)
    ImageView answerDetailPraise;
    @Bind(R.id.answer_detail_content)
    TextView answerDetailContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_detail);
        ButterKnife.bind(this);
        String s = "为什么邀请我来答这个题，因为我抽过脂还是上过手术台？窝觉得很惶恐\n" +
                "\n" +
                "如图所示，就是这样的，这也是我到医院实习之后怒减10斤体重的原因之一。\n" +
                "死胖子这个词就是用来说右边这种人的。不过我们一般也没有无良到会这么说啦。\n" +
                "比如普通外科手术的时候，切阑尾大体都还是需要3-5cm的麦氏点切口的，于是有一次一个很瘦的小姑娘切阑尾，老师评估了一下腹腔黏连不重，皮下脂肪不厚，于是做了一个1cm的切口，最多能到1.5cm，开腹也是很顺利，脂肪层可能就1cm左右的厚度吧，轻轻松松就把阑尾弄出来了。这是我这辈子见过的最小的阑尾切口了吧，大体还是要归功于小姑娘太瘦了。\n" +
                "再比如有一次做子宫阴式切除，还是直肠癌根治术，我忘记了，我真的还能记得那位大妈的肚腩，她女儿也是很瘦的，但是也不知道阿姨为什么就那么胖，腹型肥胖吧。按说，电刀切脂肪其实也是很快的，我们只要屏住气一直往下切就行了，也没什么难度除了味道不好闻之外。大妈的脂肪层，我也记不得有多厚了，切口长有10cm吧，腹壁也因为肥胖比较松，所以还没进到腹外斜肌腱膜层，外面黄色的脂肪真是淌了一台子，还往外翻着。手术的小纱布本来是用来擦血的，结果都是黄黄的脂肪，因为皮肤皮下浅筋膜本来也没多少血管，止血也快，所以真的，当时满视野都是脂肪的样子真是这辈子也不能忘。我以前也没见过活人的皮下脂肪是什么样子，然后手术的时候才发现，基本上都是脂肪粒的形状，每一粒大约有1cm*1cm*1cm大，不是均匀球形，有时候拿纱布擦一下还会把几粒带到体外来。电刀烧过的脂肪，就有点像野外烧烤的时候往炭火上浇了一勺油，刺啦一声，一股油烟冒出来，就是烤肉时候烧焦的味道，但是完全没有烤肉的香味。有一次开腹的时候凑得太近不小心猛吸了一口，觉得大约再也不会去吃烤肉了。我们医院的手术室在4楼，虽然手术室的换气做的还是很好的，但是整个4层一到手术多的时候基本上都是烤肉的味道，楼道里也是。\n" +
                "窝是不是说的有点恶心。如果吓到你们让你们觉得“卧槽这么可怕还是赶紧吃两碗红烧肉压压惊”的话，还真是对不起呢嘤。\n" +
                "\n" +
                "\n" +
                "—————————————————\n" +
                "评论区有童鞋讲哎呀我也要把皮肤割开抽个脂瘦一下，同学们我一定要跟你们讲，\n" +
                "\n" +
                "别别别！！！千万别！！！我们有看到过因为用吸尘器自己抽脂然后送命的报道！！！所以千万别自己抽脂！！！大过年的啊！！！\n" +
                "\n" +
                "编辑于 2015-02-25";
        answerDetailContent.setText(s);
        answerDetailHead.setImageURI(Uri.parse("http://www.91danji.com/attachments/201509/27/13/4cevsjye7.jpg"));
    }
}