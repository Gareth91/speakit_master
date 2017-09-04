package project.allstate.speakitvisualcommunication.volley;

import project.allstate.speakitvisualcommunication.PecsImages;

import java.util.List;

/**
 * Created by Gareth on 18/08/2017.
 */

public interface VolleyCallBackReturn {

    List<PecsImages> onSuccess(String result);
    void onError(ErrorResponse response);
}
