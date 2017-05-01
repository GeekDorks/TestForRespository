package com.mss.sponserapp.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.toolbox.ImageLoader;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.mss.sponserapp.R;
import com.mss.sponserapp.activities.CommentActivity;
import com.mss.sponserapp.activities.VideoPlayerActivityNew;
import com.mss.sponserapp.models.AllPublicPostResponce.Like;
import com.mss.sponserapp.models.AllPublicPostResponce.LikeList;
import com.mss.sponserapp.models.AllPublicPostResponce.Listpost;
import com.mss.sponserapp.models.AllPublicPostResponce.Post;
import com.mss.sponserapp.models.AllPublicPostResponce.User;
import com.mss.sponserapp.models.DeletePostModel.DeletePostServerResponse;
import com.mss.sponserapp.models.EditPostResponse.ResponsEditPost;
import com.mss.sponserapp.models.SharePostResponse.SharePostServerResponse;
import com.mss.sponserapp.models.SupportUnSupportResponce.ResponceSupportUnSupport;
import com.mss.sponserapp.utils.ApiClient;
import com.mss.sponserapp.utils.AppController;
import com.mss.sponserapp.utils.AppPreferences;
import com.mss.sponserapp.utils.AppUtils;
import com.mss.sponserapp.utils.Constants;
import com.mss.sponserapp.utils.Session;
import com.mss.sponserapp.customviews.FeedImageView;
import com.mss.sponserapp.webservices.ApiInterface;

import java.net.UnknownHostException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PublicPostAdapter extends RecyclerView.Adapter<PublicPostAdapter.MyViewHolder> {
    private List<Listpost> listpubilcPost;
    Context mcContext;
    private AppPreferences mPreferences;
    String suportTotalcount;
    boolean supt = false;
    String likeId;
    ArrayAdapter<CharSequence> adapter;
    ImageLoader imageLoader;

    private String sharePostStatus = "Share on your Homepage";

    public PublicPostAdapter(List<Listpost> listpubilcPost, Context context) {

        this.listpubilcPost = listpubilcPost;
        this.mcContext = context;
        mPreferences = new AppPreferences(mcContext);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final LinearLayout flMedia;
        FrameLayout flSharedmedia, flVideo;
        private final TextView txtPostSharedMessage;
        private TextView txtUserName, txtTimeStamp, txtPostMessage, txtSupportUnsupport, txtSupportCountbatch, txtCommentCountbatch;
        ImageView userImage, postVideo, imgeSupportUnSupport, settingImage, imgSharedvideo;
        private EditText editComment;
        FeedImageView postImage, imgSharedPhotos;
        private LinearLayout llSupportCount, llCommentCount, llShare, llSharedPosts, llsharedimagemedia;
        ProgressBar progressBar, progressBarSharedpost;

        public MyViewHolder(View itemView) {
            super(itemView);
            txtUserName = (TextView) itemView.findViewById(R.id.txt_username);
            txtTimeStamp = (TextView) itemView.findViewById(R.id.txt_timestamp);
            txtPostMessage = (TextView) itemView.findViewById(R.id.txt_post);
            txtSupportCountbatch = (TextView) itemView.findViewById(R.id.txt_support_countbatch);
            txtCommentCountbatch = (TextView) itemView.findViewById(R.id.txt_comment_countbatch);
            llSupportCount = (LinearLayout) itemView.findViewById(R.id.ll_support_unsuport);
            llCommentCount = (LinearLayout) itemView.findViewById(R.id.ll_post_comment);
            userImage = (ImageView) itemView.findViewById(R.id.img_user);
            postImage = (FeedImageView) itemView.findViewById(R.id.img_upload_photos);
            settingImage = (ImageView) itemView.findViewById(R.id.img_setting);
            imgeSupportUnSupport = (ImageView) itemView.findViewById(R.id.img_support_unspport);
            postVideo = (ImageView) itemView.findViewById(R.id.img_video);
            flMedia = (LinearLayout) itemView.findViewById(R.id.fl_media);
            flVideo = (FrameLayout) itemView.findViewById(R.id.fl_video);
            editComment = (EditText) itemView.findViewById(R.id.edit_comment);
            txtSupportUnsupport = (TextView) itemView.findViewById(R.id.txt_support_unsupport);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar_post);
            /*.............shared posts.........*/
            llSharedPosts = (LinearLayout) itemView.findViewById(R.id.ll_shared);
            llShare = (LinearLayout) itemView.findViewById(R.id.ll_share);
            txtPostSharedMessage = (TextView) itemView.findViewById(R.id.txt_post_shared);
            flSharedmedia = (FrameLayout) itemView.findViewById(R.id.fl_sharedmedia);
            imgSharedPhotos = (FeedImageView) itemView.findViewById(R.id.img_shared_photos);
            imgSharedvideo = (ImageView) itemView.findViewById(R.id.img_sharedvideo);
            progressBarSharedpost = (ProgressBar) itemView.findViewById(R.id.progressBar_sharedpost);
            llsharedimagemedia = (LinearLayout) itemView.findViewById(R.id.ll_sharedimagemedia);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_public_post, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Listpost movie = listpubilcPost.get(position);
        Post post = movie.getPost();
        User user = movie.getUser();
        final String postId = post.getId();
        final String userIdPost = post.getUserId();
        final Like like = movie.getLike();
        suportTotalcount = movie.getLikecount() + "";
        holder.flMedia.setVisibility(View.GONE);
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        if (user.getImage() != "") {
            Glide.clear(holder.userImage);
            Glide.with(mcContext).load(user.getImage()).
                    listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            if (e instanceof UnknownHostException)
                                holder.progressBar.setVisibility(View.VISIBLE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            holder.progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    }).into(holder.userImage);
        }
        if (post.getPhoto().size() > 0) {
            if (post.getShareWith().equals("0")) {
                holder.flMedia.setVisibility(View.VISIBLE);
                holder.postImage.setVisibility(View.VISIBLE);
                holder.llSharedPosts.setVisibility(View.GONE);
                holder.flSharedmedia.setVisibility(View.GONE);
                holder.progressBarSharedpost.setVisibility(View.GONE);
                if (post.getType().equals("image")) {
                    holder.flVideo.setVisibility(View.GONE);
                    Glide.clear(holder.postImage);
                    holder.postImage.setImageUrl(post.getPhoto().get(0), imageLoader);
                    holder.postImage.setVisibility(View.VISIBLE);
                    holder.postImage
                            .setResponseObserver(new FeedImageView.ResponseObserver() {
                                @Override
                                public void onError() {
                                }

                                @Override
                                public void onSuccess() {
                                }
                            });
                } else {
                    holder.flVideo.setVisibility(View.VISIBLE);
                    holder.flMedia.setVisibility(View.GONE);
                }
            } else {
                holder.flMedia.setVisibility(View.GONE);
                holder.flVideo.setVisibility(View.GONE);
                holder.progressBar.setVisibility(View.GONE);
                holder.llSharedPosts.setVisibility(View.VISIBLE);
                if (post.getType().equals("image")) {
                    holder.progressBarSharedpost.setVisibility(View.GONE);
                    holder.llsharedimagemedia.setVisibility(View.VISIBLE);
                    holder.flSharedmedia.setVisibility(View.GONE);
                    holder.imgSharedPhotos.setImageUrl(post.getPhoto().get(0), imageLoader);
                    holder.imgSharedPhotos
                            .setResponseObserver(new FeedImageView.ResponseObserver() {
                                @Override
                                public void onError() {
                                }

                                @Override
                                public void onSuccess() {
                                }
                            });
                } else {
                    holder.flSharedmedia.setVisibility(View.VISIBLE);
                    holder.llsharedimagemedia.setVisibility(View.GONE);
                }
            }
        }
        if (post.getShareWith().equals("0")) {
            holder.llSharedPosts.setVisibility(View.GONE);
            holder.txtPostMessage.setText(post.getPost());
            holder.txtUserName.setText(user.getFirstname());
        } else {
            holder.llSharedPosts.setVisibility(View.VISIBLE);
            holder.txtPostSharedMessage.setText(post.getPost());
            holder.txtPostMessage.setText(post.getShareMsg());
            if (post.getRefId() != null) {
                if (post.getRefId().equals(post.getUserId())) {
                    holder.txtUserName.setText(user.getFirstname() + " Shared their Post");
                } else {
                    holder.txtUserName.setText(user.getFirstname() + " Shared" + " " + post.getRefUsername() + " Post");
                }
            } else {
                holder.txtUserName.setText(user.getFirstname());
            }
        }
        holder.txtTimeStamp.setText(post.getCreated());
        holder.txtSupportCountbatch.setText(suportTotalcount);
        String userId = mPreferences.getPrefrenceString(Constants.USER_ID);
        Like lsk = listpubilcPost.get(position).getLike();
        if (lsk.getBoolean() != 0) {
            boolean supt = false;
            List<LikeList> list = lsk.getLikeList();
            for (LikeList like1 : list) {
                if (like1.getUserId().equals(userId)) {
                    supt = true;
                }
            }
            if (supt) {
                holder.txtSupportUnsupport.setText("UnSupport");
                holder.imgeSupportUnSupport.setImageResource(R.drawable.hand_heart);
            } else {
                holder.txtSupportUnsupport.setText("Support");
                holder.imgeSupportUnSupport.setImageResource(R.drawable.hand_empty);
            }
        }
        holder.txtCommentCountbatch.setText(movie.getCommentcount() + "");
        holder.llCommentCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gson gson = new Gson();
                Listpost movie = listpubilcPost.get(position);
                String json = gson.toJson(movie);
                Intent intentAdapaterComment = new Intent(mcContext, CommentActivity.class);
                intentAdapaterComment.putExtra("post_list", json);
                intentAdapaterComment.putExtra("check", "public");
                mcContext.startActivity(intentAdapaterComment);
            }
        });
        holder.llSupportCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPreferences = new AppPreferences(mcContext);
                boolean userLike = false;
                holder.llSupportCount.setClickable(false);
                String userId = mPreferences.getPrefrenceString(Constants.USER_ID);
                Like lsk1 = listpubilcPost.get(position).getLike();
                if (lsk1.getBoolean() != 0) {
                    List<LikeList> list = lsk1.getLikeList();
                    for (LikeList like : list) {
                        if (like.getUserId().equals(userId)) {
                            userLike = true;
                            likeId = like.getId();
                        }
                    }
                }
                ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                Call<ResponceSupportUnSupport> loginResponseData;
                if (holder.txtSupportUnsupport.getText().toString().equals("Support")) {
                    holder.txtSupportUnsupport.setText("UnSupport");
                    holder.imgeSupportUnSupport.setImageResource(R.drawable.hand_heart);
                    holder.txtSupportCountbatch.setText((listpubilcPost.get(position).getLikecount() + 1) + "");
                    loginResponseData = apiService.getSupportPostResponse(userId, postId);
                } else {
                    holder.txtSupportUnsupport.setText("Support");
                    holder.imgeSupportUnSupport.setImageResource(R.drawable.hand_empty);
                    holder.txtSupportCountbatch.setText((listpubilcPost.get(position).getLikecount() - 1) + "");
                    loginResponseData = apiService.getUnSupportPostResponse(userId, postId, likeId);
                }
                loginResponseData.enqueue(new Callback<ResponceSupportUnSupport>() {
                    @Override
                    public void onResponse(Call<ResponceSupportUnSupport> call, Response<ResponceSupportUnSupport> response) {
                        if (response.isSuccessful()) {
                            ResponceSupportUnSupport responceSupportUnSupport = response.body();
                            suportTotalcount = responceSupportUnSupport.getCount() + "";
                            holder.txtSupportCountbatch.setText(suportTotalcount);
                            com.mss.sponserapp.models.SupportUnSupportResponce.Like like1 = responceSupportUnSupport.getLike();
                            com.mss.sponserapp.models.SupportUnSupportResponce.Like_ like2 = like1.getLike();
                            LikeList likeList = new LikeList();
                            likeList.setId(like2.getId());
                            likeList.setUserId(like2.getUserId());
                            likeList.setPostId(like2.getPostId());
                            likeList.setCnt("0");
                            likeList.setCreated("yyyy-mm-dd hh:mm:ss");
                            likeList.setStatus("0");
                            Like lk = listpubilcPost.get(position).getLike();
                            lk.setBoolean(1);
                            List<LikeList> ls = lk.getLikeList();
                            if (holder.txtSupportUnsupport.getText().toString().equals("Support")) {

                                for (int cont = 0; cont < ls.size(); cont++) {

                                    if (likeId == ls.get(cont).getId()) {
                                        ls.remove(cont);
                                    }
                                }
                            } else {
                                ls.add(likeList);
                            }

                            lk.setLikeList(ls);
                            listpubilcPost.get(position).setLike(lk);
                            listpubilcPost.get(position).setLikecount(responceSupportUnSupport.getCount());
                            notifyDataSetChanged();
                            holder.llSupportCount.setClickable(true);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponceSupportUnSupport> call, Throwable t) {

                    }
                });
            }
        });

        holder.postVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Listpost movie = listpubilcPost.get(position);
                Post post = movie.getPost();
                if (post.getType().equals("video")) {
                    Intent playVideoIntent = new Intent(mcContext, VideoPlayerActivityNew.class);
                    playVideoIntent.putExtra("videoPath", post.getPhoto().get(0));
                    mcContext.startActivity(playVideoIntent);
                }
            }
        });


        holder.imgSharedvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Listpost movie = listpubilcPost.get(position);
                Post post = movie.getPost();
                if (post.getType().equals("video")) {
                    Intent playVideoIntent = new Intent(mcContext, VideoPlayerActivityNew.class);
                    playVideoIntent.putExtra("videoPath", post.getPhoto().get(0));
                    mcContext.startActivity(playVideoIntent);
                }
            }
        });

        holder.editComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gson gson = new Gson();
                Listpost movie = listpubilcPost.get(position);
                String json = gson.toJson(movie);
                Intent intentAdapaterComment = new Intent(mcContext, CommentActivity.class);
                intentAdapaterComment.putExtra("post_list", json);
                intentAdapaterComment.putExtra("check", "public");
                mcContext.startActivity(intentAdapaterComment);
            }
        });

        holder.postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Listpost movie = listpubilcPost.get(position);
                Post post = movie.getPost();
                if (post.getType().equals("image")) {
                    imageFullDialog(post.getPhoto().get(0), post.getWidth().get(0), post.getHeight().get(0));
                }
            }
        });

        holder.imgSharedPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Listpost movie = listpubilcPost.get(position);
                Post post = movie.getPost();
                if (post.getType().equals("image")) {
                    imageFullDialog(post.getPhoto().get(0), post.getWidth().get(0), post.getHeight().get(0));
                }
            }
        });

        final PopupMenu popup = new PopupMenu(mcContext, holder.settingImage);

        if (userIdPost.equals(userId)) {
            popup.inflate(R.menu.popup_mypost_menu);
        } else {
            popup.inflate(R.menu.popup_setting_menu);
        }
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.hide_post:
                        Listpost list = listpubilcPost.get(position);
                        Post post = list.getPost();
                        deletePost(position, post.getId());
                        popup.dismiss();
                        break;

                    case R.id.edit_post:
                        Listpost list1 = listpubilcPost.get(position);
                        Post post23 = list1.getPost();
                        uploadEditPost(position, post23.getId(), post23.getPost());
                        popup.dismiss();
                        break;

                    case R.id.delet_post:
                        Listpost listdelet = listpubilcPost.get(position);
                        Post postDelet = listdelet.getPost();
                        deletePost(position, postDelet.getId());
                        popup.dismiss();
                        break;
                }
                return false;
            }
        });

        holder.settingImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup.show();
            }
        });

        holder.llShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Listpost list = listpubilcPost.get(position);
                sharePost(list.getPost().getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return listpubilcPost.size();
    }

    public void sharePost(final String postId) {
        final Dialog dialog = new Dialog(mcContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_share_post);
        dialog.setCanceledOnTouchOutside(false);
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        wmlp.gravity = Gravity.CENTER;
        wmlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        Spinner spinnerPost = (Spinner) dialog.findViewById(R.id.spn_share_post);
        Button btnSharePost = (Button) dialog.findViewById(R.id.btn_share_post);
        LinearLayout llClose = (LinearLayout) dialog.findViewById(R.id.ll_cross_post);
        final EditText editMessage = (EditText) dialog.findViewById(R.id.edit_share_comment);
        adapter = ArrayAdapter.createFromResource(mcContext,
                R.array.share_post_array, R.layout.spinner_share_post);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPost.setAdapter(adapter);
        spinnerPost.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sharePostStatus = adapter.getItem(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        llClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnSharePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sharePostStatus.equals(adapter.getItem(0))) {
                    sharePostServerHit(postId, editMessage.getText().toString());
                } else if (sharePostStatus.equals(adapter.getItem(1))) {

                } else {

                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void sharePostServerHit(String postId, String message) {
        AppUtils.dialog(mcContext);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<SharePostServerResponse> deleteResponseData = apiService.getSharePost(mPreferences.getPrefrenceString(Constants.USER_ID), postId, message);
        deleteResponseData.enqueue(new Callback<SharePostServerResponse>() {
            @Override
            public void onResponse(Call<SharePostServerResponse> call, Response<SharePostServerResponse> response) {
                try {
                    if (response.isSuccessful()) {
                        AppUtils.dismissProgressDialog();
                        SharePostServerResponse serverResponse = response.body();
                        Toast.makeText(mcContext, serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                        Session.getSharePostListener();
                    } else {
                        AppUtils.dismissProgressDialog();
                    }
                } catch (Exception e) {
                    AppUtils.dismissProgressDialog();
                }
            }

            @Override
            public void onFailure(Call<SharePostServerResponse> call, Throwable t) {
                AppUtils.dismissProgressDialog();
            }
        });
    }

    public void imageFullDialog(String url, String width, String height) {
        final Dialog dialog = new Dialog(mcContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dailog_image_fullscreen);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.CENTER;
        wmlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmlp.height = WindowManager.LayoutParams.MATCH_PARENT;
        ImageView imageView = (ImageView) dialog.findViewById(R.id.img_full_screen);
        LinearLayout llCross = (LinearLayout) dialog.findViewById(R.id.ll_cross_img);
        llCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        final ProgressBar progressBar = (ProgressBar) dialog.findViewById(R.id.progress_bar_img);
        progressBar.setVisibility(View.VISIBLE);
        float widt = Float.parseFloat(width);
        float heig = Float.parseFloat(height);
        Glide.clear(imageView);

       /* imageView.getLayoutParams().height = (int) widt;
        imageView.getLayoutParams().width = (int) heig;
        imageView.requestLayout();*/

        Glide.with(mcContext).load(url).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                try {
                    progressBar.setVisibility(View.VISIBLE);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                try {
                    progressBar.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        }).into(imageView);
        dialog.show();
    }

    private void deletePost(final int position, String postId) {
        AppUtils.dialog(mcContext);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<DeletePostServerResponse> deleteResponseData = apiService.deletePost(postId);
        deleteResponseData.enqueue(new Callback<DeletePostServerResponse>() {
            @Override
            public void onResponse(Call<DeletePostServerResponse> call, Response<DeletePostServerResponse> response) {
                try {
                    if (response.isSuccessful()) {
                        AppUtils.dismissProgressDialog();
                        DeletePostServerResponse deletePostServerResponse = response.body();
                        Toast.makeText(mcContext, deletePostServerResponse.getMsg(), Toast.LENGTH_SHORT).show();
                        Log.e("data list size", "" + listpubilcPost.size());
                        listpubilcPost.remove(position);
                        notifyDataSetChanged();
                    } else {
                        AppUtils.dismissProgressDialog();
                    }
                } catch (Exception e) {
                    AppUtils.dismissProgressDialog();
                    Log.e("error ", e + "");
                }
            }

            @Override
            public void onFailure(Call<DeletePostServerResponse> call, Throwable t) {
                AppUtils.dismissProgressDialog();
                Log.e("data list", "error");
            }
        });
    }

    private void uploadEditPost(final int position, final String Id, String textPost) {
        final Dialog dialog = new Dialog(mcContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_whats_up);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.CENTER;
        wmlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        LinearLayout llCross = (LinearLayout) dialog.findViewById(R.id.ll_cross_txt);
        llCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        final EditText editPost = (EditText) dialog.findViewById(R.id.edit_post_dialog);
        editPost.setText(textPost);
        Button btnPost = (Button) dialog.findViewById(R.id.btn_post_dialog);
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // postData = editPost.getText().toString();

                updateEditPostApi(Id, editPost.getText().toString(), position);
                //   postApiHit();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void updateEditPostApi(String postId, final String editPost, final int pos) {
        AppUtils.dialog(mcContext);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponsEditPost> editResponseData = apiService.getEditPost(postId, editPost);
        editResponseData.enqueue(new Callback<ResponsEditPost>() {
            @Override
            public void onResponse(Call<ResponsEditPost> call, Response<ResponsEditPost> response) {
                try {
                    AppUtils.dismissProgressDialog();
                    if (response.isSuccessful()) {
                        ResponsEditPost responsEditPost = response.body();
                        Listpost list = listpubilcPost.get(pos);
                        Post post1 = list.getPost();
                        post1.setPost(editPost);
                        list.setPost(post1);
                        notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    AppUtils.dismissProgressDialog();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponsEditPost> call, Throwable t) {
                AppUtils.dismissProgressDialog();
            }
        });

    }


}
