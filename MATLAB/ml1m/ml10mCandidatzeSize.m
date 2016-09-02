clear all
%names=char('15','25','35','45','55','65','75','85','95','105');
names=char('15','25','35','45');
for n=1:4
filename=strcat('../ml10m/online/candidatesite/ml10mHyrcandidatesize','.txt',names(n,:));
delimiterIn=' ';
A1(:,n)=importdata(filename,delimiterIn);

filename=strcat('../ml10m/online/candidatesite/ml10mItemscandidatesize','.txt',names(n,:));
delimiterIn=' ';
A2(:,n)=importdata(filename,delimiterIn);

filename=strcat('../ml10m/online/candidatesite/ml10mKemanscandidatesize','.txt',names(n,:));
delimiterIn=' ';
A3(:,n)=importdata(filename,delimiterIn);
end


B1=A1(800000:end,:);
B2=A2(800000:end,:);
B3=A3(800000:end,:);
[M,N]=size(B1)
D=randperm(M,5000);
S1=B1(D,:);
S2=B2(D,:);
S3=B3(D,:);
x=15:10:45;
Y1=mean(S1,1);
Y2=mean(S2,1);
Y3=mean(S3,1);
for i=1:4
e1(i)=std(S1(:,i));
e2(i)=std(S2(:,i));
e3(i)=std(S3(:,i));
end
figure
hold on
errorbar(x,Y1,e1,'b-.s','MarkerSize',4);
errorbar(x,Y2,e2,'k--o','MarkerSize',4);
errorbar(x,Y3,e3,'r:p','MarkerSize',4);
box on
grid on
legend('HyRec--ml10m','Items--ml10m','KMeans--ml10m','Location','northwest')
xlabel('K');
ylabel('Candidates Size');
title('Candidates Size Online')