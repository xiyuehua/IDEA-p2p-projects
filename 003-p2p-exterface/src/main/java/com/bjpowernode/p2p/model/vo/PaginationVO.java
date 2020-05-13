package com.bjpowernode.p2p.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * ClassName:PaginationVO
 * Package:com.bjpowernode.p2p.model
 * Description
 *
 * @Date:2020/3/159:41
 * @author:xyh
 */
@Data
public class PaginationVO<T> implements Serializable {

    private List<T> LoanInfoList;

    private Long total;
}
