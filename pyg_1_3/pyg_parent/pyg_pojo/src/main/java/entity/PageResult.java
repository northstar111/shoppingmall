package entity;

import java.io.Serializable;
import java.util.List;

/**
 * 作者：杨立波
 * 时间：2018/8/31 16:04
 * 封装分页结果
 */
public class PageResult implements Serializable {

    // 分页属性：总条数和分页内容
    private long total;
    private List rows;  // List不加泛型，可扩展添加其他内容

    public PageResult(long total, List rows) {
        this.total = total;
        this.rows = rows;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List getRows() {
        return rows;
    }

    public void setRows(List rows) {
        this.rows = rows;
    }
}
