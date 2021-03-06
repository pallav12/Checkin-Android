package com.checkin.app.checkin.Account;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class AccountModel {
    @JsonProperty("pk")
    private String id;

    @JsonProperty("target_pk")
    private String targetPk;

    private ACCOUNT_TYPE accountType;

    private String pic;

    private String name;

    private ObjectNode detail;

    AccountModel() {
    }

    @Nullable
    public static AccountModel getByAccountType(@NonNull List<AccountModel> accounts, ACCOUNT_TYPE accountType) {
        for (AccountModel item : accounts) {
            if (item.getAccountType() == accountType)
                return item;
        }
        return null;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ACCOUNT_TYPE getAccountType() {
        return accountType;
    }

    @JsonProperty("acc_type")
    public void setAccountType(int accType) {
        this.accountType = ACCOUNT_TYPE.getById(accType);
    }

    public String formatAccountType() {
        switch (accountType) {
            case USER:
                return "User's account";
            case SHOP_OWNER:
                return "Owner's account";
            case SHOP_ADMIN:
                return "Admin's account";
            case RESTAURANT_MANAGER:
                return "Manager's account";
            case RESTAURANT_WAITER:
                return "Waiter's account";
            case RESTAURANT_COOK:
                return "Cook's account";
        }
        return "UNKNOWN ACCOUNT";
    }

    public String getPic() {
        return pic;
    }

    public ObjectNode getDetail() {
        return detail;
    }

    public String getTargetPk() {
        return targetPk;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null)
            return false;
        try {
            AccountModel acc = ((AccountModel) obj);
            return acc.accountType == this.accountType && acc.targetPk.equalsIgnoreCase(this.targetPk);
        } catch (ClassCastException ignored) {
            return false;
        }
    }

    public enum ACCOUNT_TYPE {
        USER(201),
        SHOP_OWNER(202), SHOP_ADMIN(203),
        RESTAURANT_MANAGER(204), RESTAURANT_WAITER(205), RESTAURANT_COOK(206);

        public final int id;

        ACCOUNT_TYPE(int id) {
            this.id = id;
        }

        public static ACCOUNT_TYPE getById(int id) {
            for (ACCOUNT_TYPE type : ACCOUNT_TYPE.values()) {
                if (type.id == id)
                    return type;
            }
            return USER;
        }
    }
}
