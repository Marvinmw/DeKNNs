clear all
figure
filename = '../Ciao/ciaoonline/CiaoItemsuserdistribution.txt';
delimiterIn = ' ';
A = importdata(filename,delimiterIn);
% h = histogram(A(:,2),10);
bar(A(:,2),0.5);
grid on
set(gca, 'xticklabel',{'0-4','4-8','8-12','12-16','16-20','20-24','24-28','28-32','32-36','36-40','40-44','44-48','48-52','52-56','>56'});
title('User Distribution on Ciao');
xlabel('Number of Clicks');
ylabel('Number of Users');