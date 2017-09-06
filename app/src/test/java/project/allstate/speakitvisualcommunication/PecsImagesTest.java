package project.allstate.speakitvisualcommunication;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Created by Gareth on 29/08/2017.
 */
public class PecsImagesTest {

    String word, category, username;
    int id, image, numberOne, numberTwo;

    @Before
    public void setUp() throws Exception {
        word = "hello";
        category = "At Home";
        username = "Gareth";
        id = 1;
        image = R.drawable.help;
        numberOne = 1;
        numberTwo = 2;
    }

    /**
     *
     */
    @Test
    public void testPecsImagesDefaultConstructor() {
        PecsImages images = new PecsImages();
        assertNotNull(images);
    }


    /**
     *
     */
    @Test
    public void testPecsImagesConstructorWithArguments() throws Exception {
        PecsImages images = new PecsImages(word, image, numberOne);
        assertNotNull(images);

        assertEquals(word, images.getWord());
        assertEquals(image, images.getImage());
        assertEquals(numberOne, images.getNumber());
    }


    @Test
    public void setImage() throws Exception {
        PecsImages images = new PecsImages();
        images.setImage(image);
        assertEquals(image, images.getImage());
    }



    @Test
    public void setId() throws Exception {
        PecsImages images = new PecsImages();
        images.setId(id);
        assertEquals(id, images.getId());
    }


    @Test
    public void setWord() throws Exception {
        PecsImages images = new PecsImages();
        images.setWord(word);
        assertEquals(word, images.getWord());
    }


    @Test
    public void setImages() throws Exception {

    }


    @Test
    public void setNumber() throws Exception {
        PecsImages images = new PecsImages();
        images.setNumber(numberOne);
        assertEquals(numberOne, images.getNumber());
    }



    @Test
    public void getCategory() throws Exception {
        PecsImages images = new PecsImages();
        images.setCategory(category);
        assertEquals(category, images.getCategory());
    }


    @Test
    public void setUserName() throws Exception {
        PecsImages images = new PecsImages();
        images.setUserName(username);
        assertEquals(username, images.getUserName());
    }

}