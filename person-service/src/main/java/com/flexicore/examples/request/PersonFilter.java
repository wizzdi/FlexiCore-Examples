package com.flexicore.examples.request;

import com.flexicore.model.FilteringInformationHolder;

public class PersonFilter extends FilteringInformationHolder {

    @Override
    public boolean supportingDynamic() {
        return true;
    }
}
