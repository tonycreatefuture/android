package com.zhongdasoft.svwtrainnet.entity;

import java.util.List;

/**
 * 项目名称： TrainNet
 * 累描述：
 * 创建人：tony
 * 创建时间：2016/12/16 11:40
 * 修改人：tony
 * 修改时间：2016/12/16 11:40
 * 修改备注：
 */

public class Profile {

    /**
     * ReturnCode : 0
     * Message : OK
     * Result : {"UserName":"d09070t050","Name":"黄炎","NextChangePasswordTime":"0001-01-01T00:00:00","SyncId":"D09070T050","Idcard":320681198110116226,"Dealer":{"NetCode":2211208,"DealerNo":74309070,"DealerName":"无锡市宝众汽车销售服务站"},"Inwork":true,"Worktype":{"ApiIdValuePairOfInt32String":[{"Id":95,"Value":"展厅经理"},{"Id":84,"Value":"培训经理"}]},"SvwStar":{"MembershipLevel":"无级别","Payment":""},"CellNumber":15821973773,"Email":""}
     */

    private int ReturnCode;
    private String Message;
    private ResultBean Result;

    public int getReturnCode() {
        return ReturnCode;
    }

    public void setReturnCode(int ReturnCode) {
        this.ReturnCode = ReturnCode;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String Message) {
        this.Message = Message;
    }

    public ResultBean getResult() {
        return Result;
    }

    public void setResult(ResultBean Result) {
        this.Result = Result;
    }

    public static class ResultBean {
        /**
         * UserName : d09070t050
         * Name : 黄炎
         * NextChangePasswordTime : 0001-01-01T00:00:00
         * SyncId : D09070T050
         * Idcard : 320681198110116226
         * Dealer : {"NetCode":2211208,"DealerNo":74309070,"DealerName":"无锡市宝众汽车销售服务站"}
         * Inwork : true
         * Worktype : {"ApiIdValuePairOfInt32String":[{"Id":95,"Value":"展厅经理"},{"Id":84,"Value":"培训经理"}]}
         * SvwStar : {"MembershipLevel":"无级别","Payment":""}
         * CellNumber : 15821973773
         * Email :
         */

        private String UserName;
        private String Name;
        private String NextChangePasswordTime;
        private String SyncId;
        private long Idcard;
        private DealerBean Dealer;
        private boolean Inwork;
        private WorktypeBean Worktype;
        private SvwStarBean SvwStar;
        private long CellNumber;
        private String Email;

        public String getUserName() {
            return UserName;
        }

        public void setUserName(String UserName) {
            this.UserName = UserName;
        }

        public String getName() {
            return Name;
        }

        public void setName(String Name) {
            this.Name = Name;
        }

        public String getNextChangePasswordTime() {
            return NextChangePasswordTime;
        }

        public void setNextChangePasswordTime(String NextChangePasswordTime) {
            this.NextChangePasswordTime = NextChangePasswordTime;
        }

        public String getSyncId() {
            return SyncId;
        }

        public void setSyncId(String SyncId) {
            this.SyncId = SyncId;
        }

        public long getIdcard() {
            return Idcard;
        }

        public void setIdcard(long Idcard) {
            this.Idcard = Idcard;
        }

        public DealerBean getDealer() {
            return Dealer;
        }

        public void setDealer(DealerBean Dealer) {
            this.Dealer = Dealer;
        }

        public boolean isInwork() {
            return Inwork;
        }

        public void setInwork(boolean Inwork) {
            this.Inwork = Inwork;
        }

        public WorktypeBean getWorktype() {
            return Worktype;
        }

        public void setWorktype(WorktypeBean Worktype) {
            this.Worktype = Worktype;
        }

        public SvwStarBean getSvwStar() {
            return SvwStar;
        }

        public void setSvwStar(SvwStarBean SvwStar) {
            this.SvwStar = SvwStar;
        }

        public long getCellNumber() {
            return CellNumber;
        }

        public void setCellNumber(long CellNumber) {
            this.CellNumber = CellNumber;
        }

        public String getEmail() {
            return Email;
        }

        public void setEmail(String Email) {
            this.Email = Email;
        }

        public static class DealerBean {
            /**
             * NetCode : 2211208
             * DealerNo : 74309070
             * DealerName : 无锡市宝众汽车销售服务站
             */

            private int NetCode;
            private int DealerNo;
            private String DealerName;

            public int getNetCode() {
                return NetCode;
            }

            public void setNetCode(int NetCode) {
                this.NetCode = NetCode;
            }

            public int getDealerNo() {
                return DealerNo;
            }

            public void setDealerNo(int DealerNo) {
                this.DealerNo = DealerNo;
            }

            public String getDealerName() {
                return DealerName;
            }

            public void setDealerName(String DealerName) {
                this.DealerName = DealerName;
            }
        }

        public static class WorktypeBean {
            private List<ApiIdValuePairOfInt32StringBean> ApiIdValuePairOfInt32String;

            public List<ApiIdValuePairOfInt32StringBean> getApiIdValuePairOfInt32String() {
                return ApiIdValuePairOfInt32String;
            }

            public void setApiIdValuePairOfInt32String(List<ApiIdValuePairOfInt32StringBean> ApiIdValuePairOfInt32String) {
                this.ApiIdValuePairOfInt32String = ApiIdValuePairOfInt32String;
            }

            public static class ApiIdValuePairOfInt32StringBean {
                /**
                 * Id : 95
                 * Value : 展厅经理
                 */

                private int Id;
                private String Value;

                public int getId() {
                    return Id;
                }

                public void setId(int Id) {
                    this.Id = Id;
                }

                public String getValue() {
                    return Value;
                }

                public void setValue(String Value) {
                    this.Value = Value;
                }
            }
        }

        public static class SvwStarBean {
            /**
             * MembershipLevel : 无级别
             * Payment :
             */

            private String MembershipLevel;
            private String Payment;

            public String getMembershipLevel() {
                return MembershipLevel;
            }

            public void setMembershipLevel(String MembershipLevel) {
                this.MembershipLevel = MembershipLevel;
            }

            public String getPayment() {
                return Payment;
            }

            public void setPayment(String Payment) {
                this.Payment = Payment;
            }
        }
    }
}
