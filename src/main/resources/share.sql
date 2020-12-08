drop table if exists ac_share_ad_cate;

/*==============================================================*/
/* Table: ac_share_ad_cate                                      */
/*==============================================================*/
create table ac_share_ad_cate
(
   ad_id                int not null auto_increment comment '海报标识',
   ad_name              varchar(20) comment '海报名称',
   ad_url               varchar(120) comment '海报模板URL',
   primary key (ad_id)
);

drop table if exists ac_share_ad_content;

/*==============================================================*/
/* Table: ac_share_ad_content                                   */
/*==============================================================*/
create table ac_share_ad_content
(
   content_id           int not null auto_increment,
   ad_id                int comment '海报标识',
   content_type         int comment '类型【1文本/2URL图片/3base64图片】',
   content_param        varchar(20) comment '参数名【由前端传】',
   start_x              int comment '起始坐标X',
   start_y              int comment '起始坐标Y',
   end_x                int comment '终止坐标X',
   end_y                int comment '终止坐标Y',
   max_line             int default 1 comment '最大行数',
   align                int default 1 comment '对齐方式【1靠左2居中3靠右】',
   color                varchar(9) comment '文本颜色',
   font_name            varchar(10) comment '字体名称',
   font_size            int comment '字体大小',
   primary key (content_id)
);
