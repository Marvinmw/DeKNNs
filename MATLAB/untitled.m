filename='./Ciao/ciaoonline/candidatesize/CiaoHyrcandidatesize.txt25';
delimiterIn=' ';
A1=importdata(filename,delimiterIn);


filename='./ml100k/onlineknn/ml100kcandidateSize/ml100kHyrcandidatesize.txt25'
delimiterIn=' ';
A2=importdata(filename,delimiterIn);



filename='./ml1m/online/candidatesite/ml10mHyrcandidatesize.txt25';
delimiterIn=' ';
A3=importdata(filename,delimiterIn);

a1=mean(A1(5000:end))
a2=mean(A2(50000:end))
a3=mean(A3(50000:end))