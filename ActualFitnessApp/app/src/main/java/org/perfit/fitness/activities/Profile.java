package org.perfit.fitness.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.perfit.fitness.R;
import org.perfit.fitness.model.UserDetails;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

class Profile extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.activity_profile, container, false);
        SessionManager userManager = new SessionManager(getContext());
        TextView userName = fragmentView.findViewById(R.id.username);
        UserDetails profileUserDetails = new UserDetails();
//        userImage = findViewById(R.id.profile_picture);

        TextView profileAge = fragmentView.findViewById(R.id.profile_age);
        TextView profileEmail = fragmentView.findViewById(R.id.profile_email);
        TextView profileGender = fragmentView.findViewById(R.id.profile_gender);
        TextView profileHeight = fragmentView.findViewById(R.id.profile_height);
        TextView profileWeight = fragmentView.findViewById(R.id.profile_weight);

        HashMap<String, String> user = userManager.getUserDetails();
        final String picture_uri = user.get(SessionManager.KEY_PROFILE_PICTURE);
        String name = user.get(SessionManager.KEY_NAME);
        String email = user.get(SessionManager.KEY_EMAIL);
        String height = user.get(SessionManager.KEY_HEIGHT);
        String weight = user.get(SessionManager.KEY_WEIGHT);
        String gender = user.get(SessionManager.KEY_GENDER);


        HashMap<String, Integer> getAge = userManager.getTimeAge();
        int age = getAge.get(SessionManager.KEY_AGE);

//        String gender = profileUserDetails.getGender();

        userName.setText("Hello \n" + name);

        profileEmail.setText("Email:\n" + email);
        profileAge.setText("Age: " + age);
        profileHeight.setText("Height: " + height);
        profileWeight.setText("Weight: " + weight);
        profileGender.setText("Gender: " + gender);

//        profileEmail.setText("Email:\n david@perfit.today");
//        profileAge.setText("Age: 24");
//        profileHeight.setText("Height: 178");
//        profileWeight.setText("Weight: 80");
//        profileGender.setText("Gender: Male");

        CircleImageView profileUserImage = fragmentView.findViewById(R.id.profile_picture);
//        profileUserImage.setImageResource(R.drawable.male_user_image);

//        try {
//            if (!picture_uri.equals("null")) {
//                new DownloadImageFromInternet(profileUserImage)
//                        .execute(profileUserDetails.getPictureId());
//            } else
        if (gender.equals("Male")) {
            profileUserImage.setImageResource(R.drawable.male_user_image);
        } else {
            profileUserImage.setImageResource(R.drawable.female_user_image);
        }
//        }catch (Exception e){
//            Log.e("ERROR",e.getMessage());
//            e.printStackTrace();
//        }

        return fragmentView;
    }


//    private class DownloadImageFromInternet extends AsyncTask<String,Void, Bitmap> {
//        CircleImageView userImage;
//
//        public DownloadImageFromInternet(CircleImageView imageView){
//            this.userImage = imageView;
//        }
//
//        @Override
//        protected Bitmap doInBackground(String... urls) {
//            String imageUrl = urls[0];
//            Bitmap bimap = null;
//            try {
//                InputStream in = new java.net.URL(imageUrl).openStream();
//                bimap = BitmapFactory.decodeStream(in);
//            }catch (Exception e){
//                e.printStackTrace();
//                Log.e("ERROR",e.getMessage());
//            }
//            return bimap;
//        }
//
//        protected void onPostExecute(Bitmap result) {
//            userImage.setImageBitmap(result);
//        }
//    }

}
