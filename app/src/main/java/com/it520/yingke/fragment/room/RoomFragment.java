package com.it520.yingke.fragment.room;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.it520.yingke.R;
import com.it520.yingke.activity.LiveShowActivity;
import com.it520.yingke.adapter.ViewerIconAdapter;
import com.it520.yingke.bean.LiveBean;
import com.it520.yingke.bean.ViewerBean;
import com.it520.yingke.bean.ViewerListBean;
import com.it520.yingke.http.RetrofitCallBackWrapper;
import com.it520.yingke.http.ServiceGenerator;
import com.it520.yingke.http.ViewerServices;
import com.it520.yingke.util.Constant;
import com.it520.yingke.util.JsonUtil;
import com.it520.yingke.util.UIUtil;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;


/* 
 * ============================================================
 * Editor: MuMuXuan(6511631@qq.com)
 *  
 * Time: 2017-05-16 21:01 
 * 
 * Description: 
 *
 * Version: 1.0
 * ============================================================
 */

public class RoomFragment extends Fragment {

    protected ArrayList<LiveBean> mLiveBeanList;
    protected int mCurrentIndex = 0;
    @BindView(R.id.recyclerView_Chat)
    RecyclerView mRecyclerViewChat;
    @BindView(R.id.iv_anchor_icon)
    SimpleDraweeView mIvAnchorIcon;
    @BindView(R.id.tv_anchor_name)
    TextView mTvAnchorName;
    @BindView(R.id.tv_online_number)
    TextView mTvOnlineNumber;
    @BindView(R.id.ll_left)
    LinearLayout mLlLeft;
    @BindView(R.id.recyclerView_Viewer)
    RecyclerView mRecyclerViewViewer;
    @BindView(R.id.tv_gold_number)
    TextView mTvGoldNumber;
    @BindView(R.id.card)
    LinearLayout mCard;
    @BindView(R.id.iv_send)
    ImageView mIvSend;
    @BindView(R.id.iv_close)
    ImageView mIvClose;
    @BindView(R.id.iv_share)
    ImageView mIvShare;
    @BindView(R.id.iv_gift_shop)
    ImageView mIvGiftShop;
    @BindView(R.id.iv_message)
    ImageView mIvMessage;
    @BindView(R.id.rl_bottom)
    RelativeLayout mRlBottom;
    @BindView(R.id.tv_anchor_number)
    TextView mTvAnchorNumber;
    @BindView(R.id.iv_msg_off)
    ImageView mIvMsgOff;
    @BindView(R.id.tv_send_msg)
    TextView mTvSendMsg;
    @BindView(R.id.edt)
    EditText mEdt;
    @BindView(R.id.edit_outer)
    RelativeLayout mEditOuter;
    @BindView(R.id.rz_content)
    RelativeLayout mRzContent;
    protected ViewerIconAdapter mViewerIconAdapter;
    protected GiftShopFragment mGiftShopFragment;
    public static final String TAG_GIFT_SHOP_FRAGMENT = "giftShopFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.frag_show, container, false);
        ButterKnife.bind(this, inflate);
        initUI();
        return inflate;
    }

    private void initUI() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(UIUtil.getContext());
        mRecyclerViewChat.setLayoutManager(linearLayoutManager);
        LinearLayoutManager horLinearLayoutManager = new LinearLayoutManager(UIUtil.getContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerViewViewer.setLayoutManager(horLinearLayoutManager);
        ArrayList<ViewerBean> viewerBeenList = new ArrayList<>();
        mViewerIconAdapter = new ViewerIconAdapter(viewerBeenList);
        mRecyclerViewViewer.setAdapter(mViewerIconAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mLiveBeanList = (ArrayList<LiveBean>) arguments.getSerializable(LiveShowActivity.LIVE_SHOW_DATA);
            mCurrentIndex = arguments.getInt(LiveShowActivity.LIVE_SHOW_INDEX);
        }
        setUI(mLiveBeanList.get(mCurrentIndex));
    }


    public void clearUI() {
        mTvAnchorNumber.setText("");
        mIvAnchorIcon.setImageResource(R.drawable.default_head);
        mViewerIconAdapter.setViewerData(new ArrayList<ViewerBean>());
    }

    public void setUI(LiveBean liveBean) {
        //设置映客直播号
        mTvAnchorNumber.setText("映客号：" + liveBean.getCreator().getId());
        //设置主播头像
        String scaledImgUrl = Constant.getScaledImgUrl(liveBean.getCreator().getPortrait(), 100, 100);
        mIvAnchorIcon.setImageURI(scaledImgUrl);
        //设置围观观众
        ViewerServices service = ServiceGenerator.getSingleton().createService(ViewerServices.class);
        Call<ResponseBody> responseBodyCall = service.getViewerData(liveBean.getId());
        responseBodyCall.enqueue(new RetrofitCallBackWrapper<ResponseBody>() {
            @Override
            public void onResponse(ResponseBody body) {
                Log.e(getClass().getSimpleName() + "xmg", "onResponse: " + "");
                try {
                    String string = body.string();
                    ViewerListBean viewerListBean = JsonUtil.parseJson(string, ViewerListBean.class);
                    mViewerIconAdapter.setViewerData(viewerListBean.getUsers());
                    Log.e(getClass().getSimpleName() + "xmg", "onResponse: " + "size: " + viewerListBean.getUsers().size());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Throwable throwable) {
            }
        });


    }

    @OnClick(R.id.iv_gift_shop)
    public void onClick() {
        //点击时，展示礼物商店
        //如果已经初始化，直接调用show，并播放内部动画
        if (mGiftShopFragment == null) {
            mGiftShopFragment = new GiftShopFragment();
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.fl_gift_shop, mGiftShopFragment, TAG_GIFT_SHOP_FRAGMENT)
                    .commit();
        } else {
            mGiftShopFragment.showContent();
        }
    }

    /**
     * 用于执行一些关闭view
     * @return 返回为true，则关闭所在的Activity
     */
    public boolean backPressed() {
        if(mGiftShopFragment!=null){
            if(mGiftShopFragment.backPressed()){
                return true;
            }
        }
        //todo 执行一些自己的EditText展示判断
        return false;
    }
}