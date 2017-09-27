package com.example.cheahhong.dbest;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.InputStream;

import static com.example.cheahhong.dbest.LoginActivity.calledPersistance;
import static com.example.cheahhong.dbest.LoginActivity.database;

public class InquiryFragment extends Fragment {

    private static final String ARG_PARAM_ID = "inquiryID";

    String inquiryID;

    private FirebaseAuth mAuth;
    FirebaseUser user ;
    DatabaseReference ref;
    static FirebaseRecyclerAdapter<Bearing, InquiryViewHolder> adapter;
    RecyclerView        mRecyclerView;
    LinearLayoutManager layoutManager;

    public InquiryFragment() {
        // Required empty public constructor
    }


    public static InquiryFragment newInstance(String param1) {
        InquiryFragment fragment = new InquiryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_ID, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        user= mAuth.getCurrentUser();

	//parameters from messageActivity
        if (getArguments() != null) {
            inquiryID = getArguments().getString(ARG_PARAM_ID);
        }

	//initialize database
        if (!calledPersistance) {
            database = FirebaseDatabase.getInstance();
            database.setPersistenceEnabled(true);
            calledPersistance = true;
        }

        ref = database.getReference("adinquiries").child(user.getUid()).child(inquiryID).child("bearings");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inquiry, container, false);

        mRecyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        displayInquiries();
        return view;
    }


    public static class InquiryViewHolder extends RecyclerView.ViewHolder{
        TextView txtSerialNo, txtHeight, txtDiameterI, txtDiameterO, txtHiddenSerial, txtComment;
        ImageView imgBearing,imgExpandCollapse;
        ProgressBar progressBar;
        CardView cv;
        RelativeLayout rl;
        public InquiryViewHolder(View itemView) {
            super(itemView);
            txtHiddenSerial = (TextView) itemView.findViewById(R.id.bearingDisplay);
            txtSerialNo = (TextView) itemView.findViewById(R.id.code);
            txtHeight = (TextView) itemView.findViewById(R.id.height);
            txtDiameterI = (TextView) itemView.findViewById(R.id.diameterI);
            txtDiameterO = (TextView) itemView.findViewById(R.id.diameterO);
            txtComment = (TextView) itemView.findViewById(R.id.extraComment);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress);
            rl = (RelativeLayout) itemView.findViewById(R.id.bearingLayout);

            cv = (CardView) itemView.findViewById(R.id.formCardView);
            imgBearing = (ImageView) itemView.findViewById(R.id.imgView);
            //imgExpandCollapse = (ImageView) itemView.findViewById(R.id.expand_collapse);
        }
    }


    private void displayInquiries() {

        adapter = new FirebaseRecyclerAdapter<Bearing, InquiryViewHolder>(
                Bearing.class,
                R.layout.bearing_view,
                InquiryViewHolder.class,
                ref
        ) {
            @Override
            protected void populateViewHolder(final InquiryViewHolder viewHolder, final Bearing model, int position) {

                viewHolder.txtHiddenSerial.setVisibility(View.GONE);

                if(model.getSerialNo()== null || model.getSerialNo().isEmpty()){
                    viewHolder.txtSerialNo.setText("Bearing #"+(position+1));
                }else{
                    viewHolder.txtSerialNo.setText(model.getSerialNo());
                }

                if(model.getExtraComment()== null || model.getExtraComment().isEmpty()){
                    viewHolder.txtComment.setText("Comment: N/A");
                }else{
                    viewHolder.txtComment.setText("Comment: " + model.getExtraComment());
                }

                //compulsory
                viewHolder.txtHeight.setText("Height: " + model.getHeight());
                viewHolder.txtDiameterI.setText("Diameter(In): " + model.getDiameterI());
                viewHolder.txtDiameterO.setText("Diameter(Out): " + model.getDiameterO());

                if(model.getImageUrl()!=null && !model.getImageUrl().equals("")){
                    viewHolder.progressBar.setVisibility(View.VISIBLE);
                    Glide.with(getActivity())
                            .load(model.getImageUrl())
                            .listener(new RequestListener<Drawable>() {

                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    viewHolder.progressBar.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    viewHolder.progressBar.setVisibility(View.GONE);
                                    return false;
                                }
                            })
                            .into(viewHolder.imgBearing);
                }


                viewHolder.imgBearing.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(viewHolder.imgBearing.getDrawable() != null) {
                            LayoutInflater factory = LayoutInflater.from(getActivity());
                            final View view = factory.inflate(R.layout.dialog_photo, null);

                            PhotoView photo = (PhotoView)view.findViewById(R.id.imgView);
                            Bitmap bitmap1=((BitmapDrawable)viewHolder.imgBearing.getDrawable()).getBitmap();
                            photo.setImageBitmap(bitmap1);

                            final AlertDialog alertadd =
                                    new AlertDialog.Builder(getActivity(),android.R.style.Theme_Black_NoTitleBar_Fullscreen)
                                            .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            })
                                            .setView(view)
                                            .create();

                            alertadd.setOnShowListener(new DialogInterface.OnShowListener() {
                                @Override
                                public void onShow(DialogInterface dialog) {
                                    ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(20);
                                    ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.WHITE);
                                }
                            });
                            alertadd.show();
                        }
                    }
                });

//                try {
//                    viewHolder.imgBearing.setImageBitmap(handleSamplingAndRotationBitmap(getActivity(), Uri.parse(model.getImageFileUri()),model.getImageFileUrl()));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

//                viewHolder. rl.setOnClickListener(new View.OnClickListener() {
//                    int count = 1;
//                    int rotationAngle=0;
//                    @Override
//                    public void onClick(View view) {
//                        Log.d("expand/collapse", String.valueOf(viewHolder.getAdapterPosition()));
//                        if (count > 0) {
//                            Log.d("count", String.valueOf(count));
//                            count++;
//                            if (count % 2 == 0) {
//                                Log.d("expand", "ya");
//                                hideBearing(viewHolder);
//                                ObjectAnimator anim = ObjectAnimator.ofFloat(viewHolder.imgExpandCollapse, "rotation",rotationAngle, rotationAngle + 180);
//                                anim.setDuration(30);
//                                anim.start();
//                                rotationAngle += 180;
//                                rotationAngle = rotationAngle%360;
//                            } else {
//                                Log.d("hide", "ya");
//
//                                showBearing(viewHolder);
//                                ObjectAnimator anim = ObjectAnimator.ofFloat(viewHolder.imgExpandCollapse, "rotation",rotationAngle, rotationAngle + 180);
//                                anim.setDuration(30);
//                                anim.start();
//                                rotationAngle += 180;
//                                rotationAngle = rotationAngle%360;
//                            }
//                        }
//                    }
//                });
            }

            public void hideBearing(InquiryViewHolder viewHolder){
                viewHolder.txtSerialNo.setVisibility(View.GONE);
                viewHolder.txtHeight.setVisibility(View.GONE);
                viewHolder.txtDiameterI.setVisibility(View.GONE);
                viewHolder.txtDiameterO.setVisibility(View.GONE);
                viewHolder.txtComment.setVisibility(View.GONE);
                viewHolder.imgBearing.setVisibility(View.GONE);
                //viewHolder.cv.setVisibility(View.GONE);

                viewHolder.txtHiddenSerial.setVisibility(View.VISIBLE);
                viewHolder.txtHiddenSerial.setText("Bearing #"+(viewHolder.getAdapterPosition()+1));
            }

            public void showBearing(InquiryViewHolder viewHolder){
                viewHolder.txtSerialNo.setVisibility(View.VISIBLE);
                viewHolder.txtHeight.setVisibility(View.VISIBLE);
                viewHolder.txtDiameterI.setVisibility(View.VISIBLE);
                viewHolder.txtDiameterO.setVisibility(View.VISIBLE);
                viewHolder.txtComment.setVisibility(View.VISIBLE);
                viewHolder.imgBearing.setVisibility(View.VISIBLE);
                //viewHolder.cv.setVisibility(View.VISIBLE);

                viewHolder.txtHiddenSerial.setVisibility(View.GONE);
            }
        };

        mRecyclerView.setAdapter(adapter);
    }

    /**
     * This method is responsible for solving the rotation issue if exist. Also scale the images to
     * 1024x1024 resolution
     *
     * @param context       The current context
     * @param selectedImage The Image URI
     * @return Bitmap image results
     * @throws IOException
     */
    public static Bitmap handleSamplingAndRotationBitmap(Context context, Uri selectedImage, String path)
            throws IOException {
        int MAX_HEIGHT = 360;
        int MAX_WIDTH = 360;

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream imageStream = context.getContentResolver().openInputStream(selectedImage);
        BitmapFactory.decodeStream(imageStream, null, options);
        imageStream.close();

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        imageStream = context.getContentResolver().openInputStream(selectedImage);
        Bitmap img = BitmapFactory.decodeStream(imageStream, null, options);

        img = rotateImageIfRequired(img, selectedImage,path);
        return img;
    }

    /**
     * Calculate an inSampleSize for use in a {@link BitmapFactory.Options} object when decoding
     * bitmaps using the decode* methods from {@link BitmapFactory}. This implementation calculates
     * the closest inSampleSize that will result in the final decoded bitmap having a width and
     * height equal to or larger than the requested width and height. This implementation does not
     * ensure a power of 2 is returned for inSampleSize which can be faster when decoding but
     * results in a larger bitmap which isn't as useful for caching purposes.
     *
     * @param options   An options object with out* params already populated (run through a decode*
     *                  method with inJustDecodeBounds==true
     * @param reqWidth  The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @return The value to be used for inSampleSize
     */
    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    /**
     * Rotate an image if required.
     *
     * @param img           The image bitmap
     * @param selectedImage Image URI
     * @return The resulted Bitmap after manipulation
     */
    private static Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage, String path) throws IOException {

        ExifInterface ei = new ExifInterface(path);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }
}
