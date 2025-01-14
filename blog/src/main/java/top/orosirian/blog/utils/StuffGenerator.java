package top.orosirian.blog.utils;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig; 
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;


public class StuffGenerator {

    /**
     * 生成前需要注意的地方：1.的配置，2.的url和密码
     * 
     * 生成后需要修改的地方
     * 1.Entity
     *      注解换成Data、AllArgsConstructor、NoArgsConstructor三件套
     *      tinyint被映射成Byte，需要改成Integer
     * 2.Mapper：去掉所有mapper的extends
     * 3.Service：（不生成service就没必要）去掉所有service和serviceImpl的extends
     * 4.xml：文件夹改名mapper，移到resources中
     *        如果已经写了一点东西，想移动entity为entity.po，则需要修改已经写好的resultMap
     * 
     * vscode控制台输出乱码，以后有机会解决
     */
    public static void main(String[] args) {
        //1.包配置
        String basePackage = "top.orosirian";
        String moduleName = "blog";
        PackageConfig packageConfig = new PackageConfig.Builder()
                                        .parent(basePackage)
                                        .moduleName(moduleName)
                                        .build();
        // 2. 数据库配置，部署到服务器上记得要改，正式上线时密码不能这么简单
        String url = "jdbc:mysql://127.0.0.1:3306/blog";
        String username = "root";
        String password = "111111";     // 部署到服务器上记得要改
        DataSourceConfig dataSourceConfig = new DataSourceConfig.Builder(url, username, password)
                                            .build();
        // 3.策略配置
        String prefix = "t_";   
        StrategyConfig strategyConfig = new StrategyConfig.Builder()
                                .enableCapitalMode()            // 开启大写命名
                                .enableSkipView()
                                .addTablePrefix(prefix)         // 把数据库表名的前缀去掉
                                .controllerBuilder()            // 不生成Controller
                                    .disable()  
                                .serviceBuilder()               // 不生成Service
                                    .disable()     
                                .entityBuilder()                // entity配置
                                    .enableFileOverride()
                                .mapperBuilder()
                                    .enableFileOverride()
                                    .mapperAnnotation(org.apache.ibatis.annotations.Mapper.class)
                                    .formatMapperFileName("%sMapper")
                                    .formatXmlFileName("%sXml")
                                .entityBuilder()
                                    .enableFileOverride()
                                    .enableLombok()     // 不写这个就会生成一大堆GetterSetter
                                .build();
        // 4.全局配置
        String baseProjectPath = System.getProperty("user.dir") + "\\blog\\src\\main\\java";  // 如果不行就手动输入真实路径
        String authorName = "orosirian";
        GlobalConfig globalConfig = new GlobalConfig.Builder()
                                            .disableOpenDir()
                                            .outputDir(baseProjectPath)
                                            .author(authorName)
                                        .build();
        
        

        // 执行生成
        ConfigBuilder configBuilder = new ConfigBuilder(packageConfig, dataSourceConfig, strategyConfig, null, globalConfig, null);
        AutoGenerator mpg = new AutoGenerator(dataSourceConfig).config(configBuilder);    // 有点丑陋的代码生成器
        mpg.execute();
    }
    
}