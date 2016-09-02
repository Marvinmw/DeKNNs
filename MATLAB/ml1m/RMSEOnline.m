clear all;
filename = '../ml10m/online/predicate/ml10mItemsdistributionpredicate.txt';
delimiterIn = ' ';
A = importdata(filename,delimiterIn);
filename = '../ml10m/online/predicate/ml10mKemansdistributionpredicate.txt';
delimiterIn = ' ';
B = importdata(filename,delimiterIn);
filename = '../ml10m/online/predicate/ml10mHyrdistributionpredicate.txt';
delimiterIn = ' ';
C = importdata(filename,delimiterIn);
D=[A(1:14,2)';B(1:14,2)';C(1:14,2)']';
figure
bar(D);
axis([0 inf 0.6 1.2]);
grid on;
set(gca, 'xticklabel',{'0-60','60-120','120-180','180-240','240-300','300-360','360-420','420-480','480-540','540-600','600-640','640-680','680-720','720-760','>760'});
%set(gca, 'xticklabel',{'1','2','3','4','5','6','7','8','9','10','11','12','13','14','15','16'});
legend('Item-ml10m','Kmeans-ml10m','HyRec-ml10m','Location','northeast');
xlabel('the Number of clicks');
ylabel('RMSE')
title('RMSE for Click Group');


figure
filename = '../ml10m/online/predicate/ml10mItemsuserdistribution.txt';
delimiterIn = ' ';
A = importdata(filename,delimiterIn);
% h = histogram(A(:,2),10);
B=A(1:14,2);
C=sum(A(15:end,2));
B(15)=C;
bar(B,0.5);
grid on
set(gca, 'xticklabel',{'0-60','60-120','120-180','180-240','240-300','300-360','360-420','420-480','480-540','540-600','600-640','640-680','680-720','720-760','>760'});
title('User Distribution of Train Data');
xlabel('Number of Clicks');
ylabel('Number of Users');