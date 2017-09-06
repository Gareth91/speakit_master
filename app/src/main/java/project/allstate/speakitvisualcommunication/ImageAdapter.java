package project.allstate.speakitvisualcommunication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Gareth on 27/07/2017.
 */

public class ImageAdapter extends BaseAdapter {

    /**
     * The context
     */
    private final Context mContext;

    /**
     * A list of all the pecs images objects
     */
    private final List<PecsImages> images;

    /**
     *
     */
    private String category;

    /**
     * This constructor is used to instantiate a ImageAdapter.
     */
    public ImageAdapter(Context context, List<PecsImages> images) {
        this.mContext = context;
        this.images = images;
    }

    /**
     * This constructor is used to instantiate a ImageAdapter.
     */
    public ImageAdapter(Context context, List<PecsImages> images, String category) {
        this.mContext = context;
        this.images = images;
        this.category = category;

    }

    /**
     * The number of cells to be rendered in the grid view
     * @return an int representing the number of cells to make in grid view
     */
    @Override
    public int getCount() {
        return images.size();
    }

    /**
     *
     * @param position
     * @return
     */
    @Override
    public Object getItem(int position) {
        return null;
    }

    /**
     *
     * @param position
     * @return
     */
    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PecsImages image = images.get(position);
        ImageView imageView;
        TextView wordView;
        String word  = image.getWord();
        ViewHolder viewHolder = null;
        // view holder pattern
        convertView = null;

        if (convertView == null) {

            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            if(mContext.getClass().equals(MainScreen.class)) {
                switch (word) {
                    case "Add Category":
                        convertView = layoutInflater.inflate(R.layout.add_category_layout, null);
                        break;
                    case "At Home":
                        convertView = layoutInflater.inflate(R.layout.at_home_layout, null);
                        break;
                    case "Favourites":
                        convertView = layoutInflater.inflate(R.layout.favourites_layout, null);
                        break;
                    case "About Me":
                        convertView = layoutInflater.inflate(R.layout.about_me_layout, null);
                        break;
                    case "Greetings":
                        convertView = layoutInflater.inflate(R.layout.greetings_layout, null);
                        break;
                    case "Food And Drink":
                        convertView = layoutInflater.inflate(R.layout.food_drink_layout, null);
                        break;
                    case "Leisure":
                         convertView = layoutInflater.inflate(R.layout.leisure_layout, null);
                        break;
                    case "Animals":
                        convertView = layoutInflater.inflate(R.layout.animals_layout, null);
                        break;
                    default:
                        convertView = layoutInflater.inflate(R.layout.personal_layout, null);
                        break;
                }
                imageView = (ImageView) convertView.findViewById(R.id.imageview);
                wordView = (TextView) convertView.findViewById(R.id.wordText);
            } else {
                switch (category) {
                    case "At Home":
                        convertView = layoutInflater.inflate(R.layout.at_home_second, null);
                        break;
                    case "Action Words":
                    case "Favourites":
                        convertView = layoutInflater.inflate(R.layout.favourites_second, null);
                        break;
                    case "About Me":
                        convertView = layoutInflater.inflate(R.layout.about_me_second, null);
                        break;
                    case "Greetings":
                        convertView = layoutInflater.inflate(R.layout.greeting_second, null);
                        break;
                    case "Food And Drink":
                        convertView = layoutInflater.inflate(R.layout.food_and_drink_second, null);
                        break;
                    case "Leisure":
                        convertView = layoutInflater.inflate(R.layout.leisure_second, null);
                        break;
                    case "Animals":
                        convertView = layoutInflater.inflate(R.layout.animals_second, null);
                        break;
                    default:
                        convertView = layoutInflater.inflate(R.layout.personal_second, null);
                        break;
                }
                imageView = (ImageView) convertView.findViewById(R.id.imageview2);
                wordView = (TextView) convertView.findViewById(R.id.wordText2);
            }
            viewHolder = new ViewHolder(imageView, wordView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }


        viewHolder.textView.setText(image.getWord());
        if (image.getWord().length() >= 12 && image.getWord().length() <= 13 && mContext.getClass().equals(SecondScreen.class) ) {
            viewHolder.textView.setTextSize(16);
        } else if (image.getWord().length() > 13 && mContext.getClass().equals(SecondScreen.class)) {
            viewHolder.textView.setTextSize(14);
        } else if (image.getWord().length() > 22 && mContext.getClass().equals(MainScreen.class)) {
            viewHolder.textView.setTextSize(25);
        }

        Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/asparagus_sprouts.ttf");
        viewHolder.textView.setTypeface(tf);

        if (image.getNumber() == 1) {
            viewHolder.imageView.setImageResource(image.getImage());
        } else {
            byte[] pecsImage = image.getImages();
            Bitmap bitmap = BitmapFactory.decodeByteArray(pecsImage, 0, pecsImage.length);
            viewHolder.imageView.setImageBitmap(bitmap);
        }

        return convertView;
    }

    /**
     * The view holder that holds references to each subview
     */
    private class ViewHolder {

        private ImageView imageView;
        private TextView textView;

        public ViewHolder(ImageView imageView, TextView textView) {
            this.imageView = imageView;
            this.textView = textView;
        }
    }
}
