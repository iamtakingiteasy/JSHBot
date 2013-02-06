package org.eientei.jshbot.api.message;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-03
 * Time: 11:49
 */
public class Message {
    private URI source;
    private URI dest;
    private String text;
    private List<TextEffect> textEffects = new ArrayList<TextEffect>();

    public Message(URI source, URI dest, String text) {
        this.source = source;
        this.dest = dest;
        this.text = text;
    }

    public Message(String source, String dest, String text) {
        this.source = URI.create(source);
        this.dest = URI.create(dest);
        this.text = text;
    }

    public void addTextEffect(TextEffect effect) {
        textEffects.add(effect);
    }

    public void removeTextEffects() {
        textEffects.clear();
    }


    public List<TextEffect> getTextEffects() {
        return Collections.unmodifiableList(textEffects);
    }

    public URI getSource() {
        return source;
    }

    public void setSource(URI source) {
        this.source = source;
    }

    public URI getDest() {
        return dest;
    }

    public void setDest(URI dest) {
        this.dest = dest;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
