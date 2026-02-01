/* swig_java_highs.i */
%module highs
%{
#include "HConfig.h"
#include "lp_data/HStruct.h"
#include "lp_data/HConst.h"
#include "util/HighsInt.h"
#include "model/HighsModel.h"
#include "lp_data/HighsStatus.h"
#include "Highs.h"
%}


%include "std_vector.i"
namespace std {
   %template(DoubleVector) vector<double>;
}

%include "carrays.i"
%array_class(double, DoubleArray);
%array_class(long long, LongLongArray);
%include "std_string.i"
%include "stdint.i"
%include "HConfig.h"
%include "lp_data/HStruct.h"
%include "lp_data/HConst.h"
%include "util/HighsInt.h"
%include "model/HighsModel.h"
%include "lp_data/HighsStatus.h"
%include "Highs.h"