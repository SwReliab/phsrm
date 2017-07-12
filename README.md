# PHSRM: Phase-Type Software Reliability Model

## Description

Software reliability growth model (SRGM) is used for evaluating the number of bugs detected in testing.
This tool provides parameter estimation and computation of reliability measures based on typical 11 models and
phase-type models. The models that the tool can be handled are

- Goel and Okumoto model (EXP, exponential model, reference 1)
- Delayed S-shaped model and its generalized model (GAMMA, gamma model, references 2,3,4)
- Modified Duane model (PARETO, pareto model, reference 5)
- Truncated normal model (TNORM, reference 6)
- Log-normal model (LNORM, reference 7)
- Inflection S-shaped model (TLOGI, truncated logistic model, reference 8)
- log-logistic model (LLOGI, reference 9)
- Gompertz model (TXVMX, truncated extreme-value max model, references 10,11)
- Frechet model (LXVMX, log-extreme-value max model, reference 11)
- Gompertz model (minimum) (TXVMI, truncated extreme-value min model, , reference 11)
- Weibull model (LXVMI, log-extreme-value min model / generalized exponential model, , references 11,12)
- Phase-type model
    - canonical model (CPH-SRM, reference 13, 15)
    - Hyper-Erlang model (HEr-SRM, reference 14, 15)

## Installation

1. Download the Java jar (phsrm-x.x.x.jar) file (download from <a href="https://github.com/okamumu/phsrm/releases/latest">Release page</a>)
1. Download jfreechart-xxxx.jar and jcommon-xxxx.jar from JFreeChart (<a href="http://www.jfree.org/jfreechart/">http://www.jfree.org/jfreechart/</a>)
1. Rename jfreechart-xxxx.jar to jfreechart.jar and jcommon-xxxx.jar to jcommon.jar and put three jar files in the same directory (classpath is set to refer jfreechart.jar and jcommon.jar of the current directory in Manifest of phsrm.jar)
1. Execute phsrm.jar (Default: `java -jar phsrm.jar 10 10`):

    ```
    java -jar phsrm.jar [number of phases in CPH-SRM] [number of phases in HEr-SRM]
    ```
1. That's all.

## How to use

#### Step 1: Preparation of data file

Make data file for the number of bugs. Please download a sample <a href="sys1.txt">sys1.txt</a>. This is Musa's data (time data) in the book (Handbook of Software Reliability Engineering by Michael R. Lyu).

The text file consists of three columns; interval time, the number of bugs, the indicator. The indicator represents whether a bug is detected just at the end of the time interval. If the indicator is set as 0, the remaining two columns are grouped data. An example of grouped data can be downloaded at <a href="sys1g.txt">sys1g.txt</a>

We give an example of grouped data. Consider the case where we collect the data that the number of bugs and its testing efforts every working day. The data is given by

| Efforts | Faults | indicator |
|:---:|:---:|:---:|
|3|10|0|
|1|5|0|
|...|...|...|

The above data means that 10 bugs are discovered with 3 testing efforts at the first working day and 5 bugs are discovered with 1 testing efforts at the second working day. The columns are separated by tabs.

#### Step 2: load the data file

Click "Open" button and select the data file.

#### Step 3: Estimation and computation

Click "Fitting" button. Estimation for all the models is started with messages.

#### Step 4: Result

Select the contents from the tree view and table in the center.
The corresponding results (graph etc.) apper in the right-side panels.

## References

1. A. L. Goel and K. Okumoto, Time-dependent error-detection rate model for software reliability and ohter performance measures, IEEE Transactions on Reliability, R-28, 206-211, 1979.
1. S. Yamada and S. Osaki, Software Reliability Growth Modeling: Models and Applications, IEEE Transactions on Software Engineering, SE-11, 1431-1437, 1985.
1. T. M. Khoshgoftaar, Nonhomogeneous Poisson processes for software reliability growth, Proceedings of the International Conference on Computational Statistics (COMPSTAT), 13-14, 1988.
1. M. Zhao and M. Xie, On Maximum Likelihood Estimation for a General non-Homogeneous Poisson Process, Scandinavian Journal of Statistics, 23, 597-607, 1996.
1. Littlewood, B., Rationale for a Modified Duane Model, IEEE Transactions on Reliability, R-33, 157-159, 1984.
1. H. Okamura and T. Dohi and S. Osaki, Software reliability growth models with normal failure time distributions, Reliability Engineering and System Safety, 116, 135-141, 2013.
1. J. A. Achcar and D. K. Dey and M. Niverthi, A Bayesian Approach Using Nonhomogeneous Poisson Processes for Software Reliability Models, Frontiers in Reliability (Eds. A. P. Basu and K. S. Basu and S. Mukhopadhyay), 1-18, World Scientific, 1998.
1. M. Ohba, Inflection S-Shaped Software Reliability Growth Model, Stochastic Models in Reliability Theory (Eds. S. Osaki and Y. Hatoyama), 144-165, Springer-Verlag, 1984.
1. S. S. Gokhale and K. S. Trivedi, Log-Logistic Software Reliability Growth Model, Proc. 3rd IEEE Int'l High-Assurance Systems Eng. Symp. (HASE-1998), 34-41, 1998.
1. S. Yamada, A stochastic software reliability growth model with Gompertz curve, Transactions of Information Processing Society of Japan (in Japanese), 37, 964-969, 1992.
1. K. Ohishi and H. Okamura and T. Dohi, Gompertz software reliability model: estimation algorithm and empirical validation, Journal of Systems and Software, 82, 535-543, 2009.
1. A. L. Goel, Software Reliability Models: Assumptions, Limitations and Applicability, IEEE Transactions on Software Engineering, SE-11, 1411-1423, 1985.
1. H. Okamura and T. Dohi, Building phase-type software reliability models, Proceedings of The 17th International Symposium on Software Reliability Engineering (ISSRE'06), pp. 289-298, IEEE Computer Society Press, 2006.
1. H. Okamura and T. Dohi, Hyper-Erlang software reliability model, Proceedings of 14th Pacific Rim International Symposium on Dependable Computing (PRDC'08), pp. 232-239, IEEE CPS, 2008.
1. [H. Okamura and T. Dohi, Phase-type software reliability model: Parameter estimation algorithms with grouped data, Annals of Operations Research, vol. 244, issue 1, pp. 177-208, 2016.](https://doi.org/10.1007/s10479-015-1870-0)

