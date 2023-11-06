package com.lyl.exceltools.index;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.sun.istack.Nullable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.compress.utils.Lists;

/**
 * @ClassName AbstractConfigIndex
 * @Description 配置索引抽象类-提供基础属性以及公共方法等
 * @author cxx-cz
 * @Date 2017年4月25日 下午3:19:10
 * @version 1.0.0
 * @param <T> T为索引关联的xml配置对象，没有关联的配置对象使用Object即可
 */
@Data
@Setter(AccessLevel.PROTECTED)
@Getter(AccessLevel.PROTECTED)
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractConfigIndex<T> {

	/** 基础配置对象实例 */
	protected T scriptInst;

	/** 基础配置文件名称-如world-config/world-config.xml */
	@Getter
	@Setter
	protected String configFilePath;

	/** 配置名称 -如world-config */
	@Getter
	private String configName;

	public AbstractConfigIndex(String configFilePath) {
		this.configFilePath = configFilePath;
		this.configName = configFilePath.split("\\/")[1].split("\\.")[0];
	}

	/**
	 * 数据已经全部放到当前实例中了， 对数据进行一些简单处理
	 * <p>
	 * 将数据加载到索引类中 ，处理数据到索引类属性里面
	 */
	public abstract void onLoadOver();

	/** 检查数据,在加载数据前执行 */
	public abstract void checkBeforeLoad() throws Exception;

	/**
	 * @Title getConfig
	 * @Description 提供根据k值获取map中配置的公共方法
	 * @param k
	 * @param map
	 * @return V
	 */
	protected <K, V> V getConfig(K k, Map<K, V> map) {
		if (map == null) {
			return null;
		}
		return map.get(k);
	}

	/**
	 * 剔除掉 list 中的 元素的 所有属性为null的元素
	 * <p>
	 * 同时清理掉 { 如果这个属性对象的每个属性都是null值 } 这样的值
	 * @param list
	 */
	protected void cleanListEleNullElements(List<?> list) {
		for (Object item : list) {
			if (item == null) {
				continue;
			}

			Class<?> class1 = item.getClass();
			for (Field field : class1.getDeclaredFields()) {
				try {
					field.setAccessible(true);
					Object object = field.get(item);
					if (object instanceof List<?>) {
						cleanListNullElements((List<?>) object);
					} else if (object instanceof Iterable<?>) {
						// do nothing.
					} else {
						if (checkIfAllFieldsNull(object)) {
							field.set(item, null);
						}
					}
				} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 内部方法。。 清理list中的不正常的元素
	 */
	private void cleanListNullElements(List<?> list) {

		if (list == null || list.isEmpty()) {
			return;
		}

		List<Object> willDeleted = new ArrayList<>(list.size());
		for (Object item : list) {
			if (item == null) {
				continue;
			}
			if (checkIfAllFieldsNull(item)) {
				willDeleted.add(item);
			}
		}

		list.removeAll(willDeleted);
	}

	/**
	 * 内部方法， 检查这个对象的所有属性是否为 null
	 * @return
	 */
	private boolean checkIfAllFieldsNull(Object object) {
		if (object == null) {
			return true;
		}

		Class<?> class1 = object.getClass();
		for (Field item : class1.getDeclaredFields()) {
			item.setAccessible(true);
			try {
				if (item.get(object) != null) {
					return false;
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		return true;
	}

	/**
	 * String转int数组，以逗号(,)切割
	 * @param str
	 * @return
	 */
	protected int[] strToIntArray(String str) {
		List<String> strList = Arrays.asList(str.split(","));
		int len = strList.size();
		int[] arr = new int[len];
		for (int index = 0; index < len; index++) {
			String s = strList.get(index);
			arr[index] = Integer.parseInt(s);
		}
		return arr;
	}
}
