package main.service;

import main.utils.Utils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import pl.media4u.bonprix.memcached.annotations.*;
import pl.media4u.bonprix.memcached.util.MemcacheKeyLength;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tomek on 28.04.17.
 */
public class LongRununigServie {

    public static final String NAMESPACE = "LongRununigServie-namespace";

    //klucz
    //jesli jest namespace to sa dwa klucze
    //jeden dla glowny_prefix - namespace + params jak są => to zwraca atualny czas time_stamp / tomek-dom-LongRununigServie lub z params tomek-dom-LongRununigServie-asd
    //drugi dla faktycznych danych, ten klucz ma postać:
    //glowny_prefix - nazwa_metody - time_stamp - parametry metody / tomek-dom-loadDataNamespacePrefixAndParam-1493453732648-asd

    //jesli nie ma namespace
    //glowny_prefix - nazwa_metody - parametry metody / tomek-dom-onlyCachce-asd

    @CachedMethod
    @TimeToLive(MemcacheKeyLength.SHORT)
    @NamespacePrefix(NAMESPACE) // to wyczysci tylko clearCache(@NamespacePrefixParam String param) z pasujacym param
    public String loadDataNamespacePrefixAndParam(@NamespacePrefixParam String param) {
        Utils.sleep(2);
        return "loadDataNamespacePrefixAndParam " + param;
    }

    @CachedMethod
    @NamespacePrefix(NAMESPACE) // to wyczysci tylko clearAllCache() ale dla wszystkich kluczy w namespace
    public String simpleDataNamespacePrefixWithParams(String str) {
        Utils.sleep(2);
        return "simpleDataNamespacePrefixWithParams " + str;
    }

    @CachedMethod
    @NamespacePrefix(NAMESPACE) // to wyczysci tylko clearAllCache() ale dla wszystkich kluczy w namespace
    public Map<String, Long> simpleDataNamespacePrefixNoParams() {
        Utils.sleep(2);
        //return "simpleDataNamespacePrefixNoParams";
        return new HashMap<>();
    }

    @CachedMethod
    @NamespacePrefix(NAMESPACE) // to wyczysci tylko clearAllCache() ale dla wszystkich kluczy w namespace
    public String simpleDataNamespacePrefixNoParamsString() {
        Utils.sleep(2);
        return "simpleDataNamespacePrefixNoParamsString";
    }

    @CachedMethod // to wyczysci tylko flush all
    public String onlyCachce(String str) {
        Utils.sleep(2);
        return "onlyCachce " + str;
    }

    @InvalidateNamespace(NAMESPACE)
    public void clearCache(@NamespacePrefixParam String param) {}

    @InvalidateNamespace(NAMESPACE)
    public void clearAllCache() {}

}
