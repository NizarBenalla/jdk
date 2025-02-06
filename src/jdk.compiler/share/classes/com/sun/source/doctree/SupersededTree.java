package com.sun.source.doctree;

import java.util.List;

public interface SupersededTree extends BlockTagTree{
    List<? extends DocTree> getReference();
}
