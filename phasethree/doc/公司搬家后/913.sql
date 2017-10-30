--修改任务描述动态变更.以前为<span>%s</span>修改了任务<span>%s</span>(而且只显示前20个字符).
--现在改为显示全部,并显示原先内容

update T_DYNAMIC_MODULE set formatStr='<span>%s</span>把任务<span>%s</span>修改为<span>%s</span>' where moduleType = 'TASK_UPDATE';