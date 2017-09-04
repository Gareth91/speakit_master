package project.allstate.speakitvisualcommunication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import project.allstate.speakitvisualcommunication.R;

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
     * This constructor is used to instantiate a ImageAdapter.
     */
    public ImageAdapter(Context context, List<PecsImages> images) {
        this.mContext = context;
        this.images = images;
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
        final PecsImages image = images.get(position);
        final ImageView imageView;
        final TextView wordView;

        // view holder pattern
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            if(mContext.getClass().equals(MainScreen.class)) {
                convertView = layoutInflater.inflate(R.layout.activity_linear_layout_image, null);
                imageView = (ImageView) convertView.findViewById(R.id.imageview);
                wordView = (TextView) convertView.findViewById(R.id.wordText);
            } else {
                convertView = layoutInflater.inflate(R.layout.activity_linear_layout_image2, null);
                imageView = (ImageView) convertView.findViewById(R.id.imageview2);
                wordView = (TextView) convertView.findViewById(R.id.wordText2);
            }
            final ViewHolder viewHolder = new ViewHolder(imageView, wordView);
            convertView.setTag(viewHolder);
        }

        final ViewHolder viewHolder = (ViewHolder)convertView.getTag();
        viewHolder.textView.setText(image.getWord());
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

        private final ImageView imageView;
        private final TextView textView;

        public ViewHolder(ImageView imageView, TextView textView) {
            this.imageView = imageView;
            this.textView = textView;
        }
    }
}
