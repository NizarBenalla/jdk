package jdk.javadoc.internal.doclets.formats.html.taglets;

import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.SupersededTree;
import jdk.javadoc.doclet.Taglet;

import jdk.javadoc.internal.doclets.formats.html.HtmlConfiguration;
import jdk.javadoc.internal.html.Content;
import jdk.javadoc.internal.html.ContentBuilder;
import jdk.javadoc.internal.html.HtmlTree;

import javax.lang.model.element.Element;
import java.util.EnumSet;
import java.util.List;

public class SupersededTaglet extends BaseTaglet {

    SupersededTaglet(HtmlConfiguration config) {
        super(config, DocTree.Kind.SUPERSEDED, false, EnumSet.allOf(Taglet.Location.class));
    }

    @Override
    public Content getAllBlockTagOutput(Element holder, TagletWriter tagletWriter) {
        var ch = utils.getCommentHelper(holder);
        var htmlWriter = tagletWriter.htmlWriter;
        this.tagletWriter = tagletWriter;
        List<? extends DocTree> tags = utils.getBlockTags(holder, getName());
        System.out.println(getName());
        return getOutput(holder, tags);

    }

    private Content getOutput(Element element, List<? extends DocTree> tags) {
        Content content = new ContentBuilder();
        HtmlTree ul =  HtmlTree.UL();
        ul.add( new ContentBuilder(config.contents.getContent("doclet.s")));
        for (DocTree tag : tags) {
                ul.add( HtmlTree.LI()
                        .add(config.contents.getContent("doclet.s.api",tag)));
        }
        content.add(ul);

        return content;
    }
}
