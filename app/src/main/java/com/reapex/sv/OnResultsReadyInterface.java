package com.reapex.sv;

import java.util.ArrayList;

public interface OnResultsReadyInterface {
    void onResults(ArrayList<String> results);
    void onFinsh();
    void onError(int error);
}
