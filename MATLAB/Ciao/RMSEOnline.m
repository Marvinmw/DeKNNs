clear all;
filename = '../Ciao/ciaoonline/predicate/CiaoItemsdistributionpredicate.txt';
delimiterIn = ' ';
A = importdata(filename,delimiterIn);
filename = '../Ciao/ciaoonline/predicate/CiaoKmeansdistributionpredicate.txt';
delimiterIn = ' ';
B = importdata(filename,delimiterIn);
filename = '../Ciao/ciaoonline/predicate/CiaoHyrdistributionpredicate.txt';
delimiterIn = ' ';
C = importdata(filename,delimiterIn);
D=[A(1:15,2)';B(1:15,2)';C(1:15,2)']';
figure
bar(D);
axis([0 inf 0 1.2]);
grid on;
set(gca, 'xticklabel',{'0-4','4-8','8-12','12-16','16-20','20-24','24-28','28-32','32-36','36-40','40-44','44-48','48-52','52-56','>56'});
%set(gca, 'xticklabel',{'1','2','3','4','5','6','7','8','9','10','11','12','13','14','15','16'});
legend('Item','Kmeans','HyRec','Location','northeast');
xlabel('the Number of clicks');
ylabel('RMSE')
title('RMSE for Click Group');


figure
filename = '../Ciao/ciaoonline/predicate/CiaoItemsuserdistribution.txt';
delimiterIn = ' ';
A = importdata(filename,delimiterIn);
% h = histogram(A(:,2),10);
bar(A(:,2),0.5);
grid on
set(gca, 'xticklabel',{'0-4','4-8','8-12','12-16','16-20','20-24','24-28','28-32','32-36','36-40','40-44','44-48','48-52','52-56','>56'});
title('User Distribution of Train Data');
xlabel('Number of Clicks');
ylabel('Number of Users');